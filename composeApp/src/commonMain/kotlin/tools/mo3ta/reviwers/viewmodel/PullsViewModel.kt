package tools.mo3ta.reviwers.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tools.mo3ta.reviwers.model.Coverage
import tools.mo3ta.reviwers.model.IssueComments
import tools.mo3ta.reviwers.model.PullRequest
import tools.mo3ta.reviwers.model.Review
import tools.mo3ta.reviwers.model.User
import tools.mo3ta.reviwers.model.UserReviews
import tools.mo3ta.reviwers.screens.PullsScreenData
import tools.mo3ta.reviwers.utils.formatDate
import tools.mo3ta.reviwers.utils.getCoverage
import tools.mo3ta.reviwers.utils.lastDate


data class UserContribution(
    val user: String,
    val approved: List<UserReviews>,
    val commented: List<UserReviews>,
    val receivedComments: List<UserReviews>,
    val created: List<UserReviews>,
    val quality: List<Coverage>,
){
    fun  getQualityPercentage(): String {
        return if (quality.isNotEmpty()){
            "${quality.map { it.quality }.average().toInt()}%"
        }else{
            "N/A"
        }

    }
}

data class PullsUiState(
    val currentPage: Int = 1,
    val pulls: Int = 0,
    val data: List<UserContribution> = emptyList(),
    val isLoading:Boolean = false,
    val isNextShown:Boolean = true,
    val lastDate :String = "",
    val sortTypes: SortTypes = SortTypes.APPROVALS,
)

enum class SortTypes {
    APPROVALS, COMMENTS, REVIEWED, QUALITY
}

const val APPROVE= "APPROVED";
const val CREATED= "CREATED";
const val COMMENETS= "COMMENTED";
const val COMMENETS_RECEIVED= "comments_received";

class PullsViewModel(data: PullsScreenData) : ViewModel() {



    private val githubKey = data.apiKey
    private val ownerWithRepo  = data.ownerWithRepo
    private val isEnterprise  = data.isEnterprise
    private val enterprise  = data.enterprise
    private val pageSize = data.pageSize
    private val lastPageNumber = data.lastPageNumber

    private val urlPrefix = prepareUrl(isEnterprise, enterprise)

    private var totalReviews:MutableList<UserReviews> = mutableListOf()
    private var totalCoverage:MutableList<Coverage> = mutableListOf()

    private val _uiState = MutableStateFlow(PullsUiState())
    val uiState = _uiState.asStateFlow()

    private val allUserContinuations = mutableListOf<UserContribution>()
    private val allPullRequests = mutableListOf<PullRequest>()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json = Json {
                ignoreUnknownKeys = true
            })
            }
        }

    init {
         getPulls()
    }

    fun getPulls() {
        _uiState.update {
            it.copy(isLoading = true)
        }

        val handler = CoroutineExceptionHandler { _, _ ->
            _uiState.update {
               it.copy(isLoading = false)
            }
        }

        viewModelScope.launch(handler) {
            val pullRequests = loadPullRequests(_uiState.value.currentPage)

            allPullRequests.addAll(pullRequests)

            val lastDate = lastDate(pullRequests.map { formatDate(it.created_at ?: "") })

            _uiState.update { it ->
                it.copy(isNextShown = pullRequests.size == pageSize, lastDate = lastDate)
            }

            val requests = pullRequests.map {
                val reviewJob = launch {
                    try {
                        val reviews = async {  loadReviews(it) }.await()
                        //val reviews = loadReviews(it)
                        totalReviews.addAll(reviews)
                    }catch (e :Exception){
                        println("error , reviews  ${it.number}")
                    }

                }
                val commentsJob = launch {
                    try {
                        val comments = async {  loadComments(it.number, it.user) }.await()
                        // val comments = loadComments(it.number, it.user)
                        totalReviews.addAll(comments)
                    }catch (e :Exception){
                        println("error , comments  ${it.number}")
                    }
                }

                val qualityJob = launch {
                    try {
                        // val coverages = async {  loadCoverages(it.number, it.user)}.await()
                        val coverages = loadCoverages(it.number, it.user)
                        totalCoverage.addAll(coverages)
                    }catch (e :Exception){
                        println("error , totalCoverage $e")
                    }

                }
                listOf(reviewJob , qualityJob, commentsJob)
            }
            try {
                requests.flatten().joinAll()
            }catch (_: CancellationException){

            }catch (e: JsonConvertException){
                _uiState.update {
                  it.copy(isLoading = false)
                }
                return@launch
            }


            val data = totalReviews.groupBy { it.user }.map { (user, work) ->
                UserContribution(user,
                    approved =  work.filter { it.state == APPROVE },
                    commented = work.filter { it.state == COMMENETS} ,
                    receivedComments = totalReviews.filter { it.commentsTo == user }.map { it.copy(state = COMMENETS_RECEIVED) },
                    created = work.filter { it.state == CREATED },
                    quality = totalCoverage.filter { it.user == user }
                )
            }.sortedByDescending { sort(_uiState.value.sortTypes, it) }

            allUserContinuations.clear()
            allUserContinuations.addAll(data)

            _uiState.update {
                val totalCount = it.pulls + pullRequests.size
                it.copy(pulls = totalCount ,data = allUserContinuations , isLoading = false , currentPage = (_uiState.value.currentPage + 1))
            }

            if (_uiState.value.currentPage < lastPageNumber){
                getPulls()
            }
        }
    }

    private suspend  fun loadPullRequests(page:Int = 1): List<PullRequest> {
        return httpClient
            .get("https://$urlPrefix/repos/${ownerWithRepo}/pulls") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $githubKey")
                }
                parameter("state" , "closed")
                parameter("sort", "updated")
                parameter("direction", "desc")
                parameter("per_page" , "$pageSize")
                parameter("page" , page)
            }.body()

    }

    private fun prepareUrl(isEnterprise: Boolean, enterprise: String): String {
        return if(isEnterprise){
            "github.${enterprise}.com/api/v3"
        }else{
            "api.github.com"
        }
    }

    private suspend fun loadReviews(pullRequest: PullRequest): List<UserReviews> {
        val allReviews =  httpClient
            .get("https://$urlPrefix/repos/${ownerWithRepo}/pulls/${pullRequest.number}/reviews") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $githubKey")
                }
            }
            .body<List<Review>>().filter { it.state == APPROVE  && it.user != null }

        val allData = mutableListOf<UserReviews>()
        val approvals = allReviews.map {
               UserReviews(it.user!!.login, APPROVE, date = it.submitted_at)
        }

        allData.addAll(approvals)
        allData.add(UserReviews(pullRequest.user.login, CREATED, date = pullRequest.created_at)) // TODO add date of creation PR

        return allData
    }

    private suspend fun loadComments(number: Int, user: User): List<UserReviews> {
        val allComments =  httpClient
            .get("https://$urlPrefix/repos/${ownerWithRepo}/pulls/${number}/comments") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $githubKey")
                }
            }
            .body<List<Review>>()
            .filter { it.user != null && user.login != it.user.login }
            .map { UserReviews(it.user!!.login, state = COMMENETS, date = it.updated_at, body = it.body, commentsTo = user.login)}

        return allComments
    }

    private suspend fun loadCoverages(number: Int, user: User): List<Coverage> {
       val coverageComment = httpClient
            .get("https://$urlPrefix/repos/${ownerWithRepo}/issues/${number}/comments") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $githubKey")
                }
            }
            .body<List<IssueComments>>().lastOrNull { it.user.login == "sonarqube-prod-integration[bot]" }

        val coverage = coverageComment?.body?.getCoverage()

        return mutableListOf<Coverage>().apply {
            coverageComment?.let { comment ->
                coverage?.let {
                    add(Coverage(user.login,it, updated_at = comment.updated_at ))
                }
            }

        }
    }

    fun onChangeFilter(type: SortTypes) {
        _uiState.update { state ->
            state.copy(sortTypes = type, data = state.data.sortedByDescending { sort(type, it) })
        }
    }

    private fun sort(sortTypes: SortTypes, it: UserContribution) = when (sortTypes.name) {
        SortTypes.APPROVALS.name -> it.approved.size
        SortTypes.COMMENTS.name -> it.commented.size
        SortTypes.QUALITY.name -> if (it.quality.isEmpty()) 0 else it.quality.map { it.quality }.average().toInt()
        else -> it.receivedComments.size
    }

    fun onSearchQuery(query:String){
        val result = if (query.isBlank()){
           allUserContinuations
        }else {
            allUserContinuations.filter { it.user.lowercase().contains(query) }
        }
        _uiState.update {
            it.copy(data = result)
        }
    }

    override fun onCleared() {
        httpClient.close()
    }
}


