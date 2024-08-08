package tools.mo3ta.reviwers.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tools.mo3ta.reviwers.utils.getTimeRange

@Serializable
data class PullRequest(val number:Int,
                       val title: String,
                       val user: User,
                       val created_at :String? = null,
                       val updated_at :String? = null,
                       val closed_at :String? = null,
                       val merged_at :String? = null,
    ){

    val mergeTime = if (created_at != null && merged_at != null){
        getTimeRange(created_at, merged_at)
    }else -1L
}

@Serializable
data class Review(
    val user: User? = null, // some calls not return data for user so it is nullable.
    val state: String?=null,
    val updated_at: String?= null,
    val submitted_at: String?= null,
    val body: String?= null,
)

@Serializable
data class UserReviews(
    val user: String,
    val state: String?=null,
    val date: String?= null,
    val body: String?= null,
    val commentsTo: String?=null,
)



@Serializable
data class IssueComments(
    val user: User,
    val updated_at: String?= null,
    val body: String?= null,
)

@Serializable
data class User(
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl:String,
)

data class Coverage(val user: String, val quality: Float, val updated_at: String?= null,)
