package tools.mo3ta.reviwers.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
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
import tools.mo3ta.reviwers.utils.getCoverage


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

    val pageSize = 100

    private val githubKey = data.apiKey
    private val ownerWithRepo  = data.ownerWithRepo
    private val isEnterprise  = data.isEnterprise
    private val enterprise  = data.enterprise

    private var totalReviews:MutableList<UserReviews> = mutableListOf()
    private var totalCoverage:MutableList<Coverage> = mutableListOf()

    private val _uiState = MutableStateFlow(PullsUiState())
    val uiState = _uiState.asStateFlow()

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

        viewModelScope.launch {
            val pullRequests = loadPullRequests(_uiState.value.currentPage)

            _uiState.update {
                it.copy(isNextShown = pullRequests.size == pageSize)
            }

            val requests = pullRequests.map {
                val reviewJob = launch {
                   // val reviews = async {  loadReviews(it.number, it.user) }.await()
                    val reviews = loadReviews(it)
                    totalReviews.addAll(reviews)
                }
                val commentsJob = launch {
                    // val reviews = async {  loadReviews(it.number, it.user) }.await()
                    val comments = loadComments(it.number, it.user)
                    totalReviews.addAll(comments)
                }

                val qualityJob = launch {
                   // val coverages = async {  loadCoverages(it.number, it.user)}.await()
                    val coverages = loadCoverages(it.number, it.user)
                    totalCoverage.addAll(coverages)
                }
                listOf(reviewJob , qualityJob, commentsJob)
            }
            requests.flatten().joinAll()

            val data = totalReviews.groupBy { it.user }.map { (user, work) ->
                UserContribution(user,
                    approved =  work.filter { it.state == APPROVE },
                    commented = work.filter { it.state == COMMENETS} ,
                    receivedComments = totalReviews.filter { it.commentsTo == user }.map { it.copy(state = COMMENETS_RECEIVED) },
                    created = work.filter { it.state == CREATED },
                    quality = totalCoverage.filter { it.user == user }
                )
            }.sortedByDescending { sort(_uiState.value.sortTypes, it) }

            _uiState.update {
                val totalCount = it.pulls + pullRequests.size
                it.copy(pulls = totalCount ,data = data , isLoading = false , currentPage = (_uiState.value.currentPage + 1))
            }
        }
    }

    private suspend  fun loadPullRequests(page:Int = 1): List<PullRequest> {
        val urlPrefix = prepareUrl(isEnterprise, enterprise)
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
        val urlPrefix = prepareUrl(isEnterprise, enterprise)
        val allReviews =  httpClient
            .get("https://$urlPrefix/repos/${ownerWithRepo}/pulls/${pullRequest.number}/reviews") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $githubKey")
                }
            }
            .body<List<Review>>().filter { it.state == APPROVE }

        val allData = mutableListOf<UserReviews>()
        val approvals = allReviews.map {
               UserReviews(it.user.login, APPROVE, date = it.submitted_at)
        }

        allData.addAll(approvals)
        allData.add(UserReviews(pullRequest.user.login, CREATED, date = pullRequest.created_at)) // TODO add date of creation PR

        return allData
    }

    private suspend fun loadComments(number: Int, user: User): List<UserReviews> {
        val urlPrefix = prepareUrl(isEnterprise, enterprise)
        val allComments =  httpClient
            .get("https://$urlPrefix/repos/${ownerWithRepo}/pulls/${number}/comments") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $githubKey")
                }
            }
            .body<List<Review>>()
            .filter { it.user.login != user.login }
            .map { UserReviews(it.user.login, state = COMMENETS, date = it.updated_at, body = it.body, commentsTo = user.login)}

        return allComments
    }

    private suspend fun loadCoverages(number: Int, user: User): List<Coverage> {
        val urlPrefix = prepareUrl(isEnterprise, enterprise)
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


    override fun onCleared() {
        httpClient.close()
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
}


