package tools.mo3ta.reviwers.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import tools.mo3ta.reviwers.model.PullRequest
import tools.mo3ta.reviwers.utils.formatDateAsDateWithDayName
import tools.mo3ta.reviwers.utils.mapHoursToTime
import tools.mo3ta.reviwers.viewmodel.DayActivity


@Composable
fun DayActivityItem(data: DayActivity) {
    Card (modifier = Modifier.fillMaxWidth(0.5f).padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ){
        Column (
            Modifier.padding(8.dp)){
            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                Text(data.date)
                Text(data.dayOnly, fontWeight = Bold)
            }
            Spacer(
                Modifier.size(8.dp))
            Text("Opened: ${data.openedCount}", color = Color(0xff76916e))
            Text("Merged: ${data.mergedCount}", color = Color(0xff1260bb))
        }
    }
}

@Composable
fun PullRequestMergeItem(data: PullRequest) {
    Card (modifier = Modifier.fillMaxWidth(0.5f).padding(bottom = 16.dp), colors = CardDefaults.cardColors(
        containerColor = Color.White
    ),
        elevation = CardDefaults.cardElevation(8.dp)
        ){
        Column (
            Modifier.padding(8.dp)){
            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                Text("#${data.number}")
                Text(data.user.login)
            }
            Spacer(Modifier.size(8.dp))
            Text("Created: ${formatDateAsDateWithDayName(data.created_at ?: "")}")
            Spacer(Modifier.size(8.dp))
            Text("Merged: ${formatDateAsDateWithDayName(data.merged_at ?: "")}")
            Spacer(Modifier.size(8.dp))
            Text("Merge Time: ${mapHoursToTime(data.mergeTime)}", fontWeight = Bold, color = Color(0xff1260bb))
        }
    }
}

