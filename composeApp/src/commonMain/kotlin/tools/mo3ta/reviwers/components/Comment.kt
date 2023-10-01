package tools.mo3ta.reviwers.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import tools.mo3ta.reviwers.model.UserReviews
import tools.mo3ta.reviwers.utils.formatDate

@Composable
fun Comments(comment: UserReviews, myComments: Boolean) {
    Column(modifier = Modifier.padding(16.dp)) {
        val text = if (myComments) comment.commentsTo else comment.user
        Text( text ?: "" )
        Card(modifier = Modifier.fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
        ) {
            Box(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)){
                Text(comment.body ?: "")
            }
        }
        Text(formatDate(comment.date ?: ""))
    }
}