package tools.mo3ta.reviwers.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequest(val number:Int,
                       val title: String,
                       val user: User,
                       val created_at :String? = null,
                       val updated_at :String? = null,
                       val closed_at :String? = null,
    )

@Serializable
data class Review(
    val user: User,
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
