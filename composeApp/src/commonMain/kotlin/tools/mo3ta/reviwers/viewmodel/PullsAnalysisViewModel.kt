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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tools.mo3ta.reviwers.model.PullRequest
import tools.mo3ta.reviwers.screens.PullsData
import tools.mo3ta.reviwers.utils.formatDate
import tools.mo3ta.reviwers.utils.formatDateAsDateWithDayName
import tools.mo3ta.reviwers.utils.lastDate
import tools.mo3ta.reviwers.utils.mapHoursToTime


data class PullsAnalysisUiState(
        val currentPage: Int = 1,
        val pulls: Int = 0,
        val dayActivities : List<DayActivity> = emptyList(),
        val mergeTime : List<PRMergeTime> = emptyList(),
        val data: List<PullRequest> = emptyList(),
        val isLoading:Boolean = false,
)

data class DayActivity(val day:String, val openedCount : Int = 0 , val mergedCount : Int = 0){
    val dayOnly = day.split(" ").lastOrNull() ?: ""
    val date = day.split(" ").firstOrNull() ?: ""
}
data class PRMergeTime(val number:String, val time : Long = 0) {
    val representedTime = mapHoursToTime(time)
}


class PullsAnalysisViewModel(data: PullsData) : ViewModel() {

    private val githubKey = data.apiKey
    private val ownerWithRepo  = data.ownerWithRepo
    private val isEnterprise  = data.isEnterprise
    private val enterprise  = data.enterprise
    private val pageSize = data.pageSize
    private val lastPageNumber = data.lastPageNumber

    private val urlPrefix = prepareUrl(isEnterprise, enterprise)

    private val _uiState = MutableStateFlow(PullsAnalysisUiState())
    val uiState = _uiState.asStateFlow()

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
        val handler = CoroutineExceptionHandler { _, _ ->
            _uiState.update {
                it.copy(isLoading = false)
            }
        }

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch(handler) {
            val pullRequests = loadPullRequests(_uiState.value.currentPage)

            allPullRequests.addAll(pullRequests)

            val mergeGroup = allPullRequests
                .filter { it.merged_at != null  }
                .groupBy { formatDateAsDateWithDayName(it.merged_at ?: "") }

            val creationGroup = allPullRequests
                .filter { it.created_at != null  }
                .groupBy { formatDateAsDateWithDayName(it.created_at ?: "") }

            val allDays = mergeGroup.keys + creationGroup.keys

            val dayActivities = allDays.map {
                   DayActivity(
                       it,
                       creationGroup[it]?.size ?: 0,
                       mergeGroup[it]?.size ?: 0,
                   )
            }

            _uiState.update {
                it.copy(pulls = allPullRequests.size ,
                    data = allPullRequests.filter { it.mergeTime != -1L } ,
                    dayActivities = dayActivities,
                    isLoading = false ,
                    currentPage = (_uiState.value.currentPage + 1))
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

    override fun onCleared() {
        httpClient.close()
    }

    fun sort(isAsc: Boolean) {
        val data = allPullRequests.filter { it.mergeTime != -1L }
        _uiState.value = _uiState.value.copy(
            data = if (isAsc) data.sortedBy { it.mergeTime } else data.sortedByDescending { it.mergeTime }
        )
    }

    fun onSearchQuery(query: String) {
        val data = allPullRequests.filter { it.mergeTime != -1L }
        _uiState.value = _uiState.value.copy(
            data = data.filter { it.user.login.contains(query) }
        )
    }

    fun sortActivity(isAsc: Boolean) {
        _uiState.value.dayActivities.let { data ->
            _uiState.value = _uiState.value.copy(
                dayActivities = if (isAsc) data.sortedBy { it.openedCount } else data.sortedByDescending { it.openedCount }
            )
        }
    }
}


