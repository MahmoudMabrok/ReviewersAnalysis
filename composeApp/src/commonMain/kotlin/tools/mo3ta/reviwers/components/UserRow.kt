package tools.mo3ta.reviwers.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tools.mo3ta.reviwers.viewmodel.UserContribution

@Composable
fun UserItem(data: UserContribution){
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

//            val painter = asyncPainterResource(data.user.avatarUrl)
//
//            KamelImage(
//                resource = painter,
//                contentDescription = "image",
//                modifier = Modifier.width(50.dp).height(50.dp).background(color = Color.Blue),
//                contentScale = ContentScale.Crop,
//            )
            Text(text = data.user , fontSize = 24.sp, textAlign = TextAlign.Center)
            Text("Created: ${data.created.size}")
            Text("Approved: ${data.approved.size}")
            Text("Commented: ${data.commented.size}")
            Text("Received: ${data.receivedComments.size}")
            Text("Quality: ${data.getQualityPercentage()}")

        }

    }

}
