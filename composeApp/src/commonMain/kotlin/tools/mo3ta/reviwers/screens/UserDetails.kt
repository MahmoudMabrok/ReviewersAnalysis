package tools.mo3ta.reviwers.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import tools.mo3ta.reviwers.components.Comments
import tools.mo3ta.reviwers.components.Divider
import tools.mo3ta.reviwers.viewmodel.UserContribution

data class UserDetails(val userData: UserContribution ) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var comments by rememberSaveable { mutableStateOf(userData.commented) }
        var myComments by rememberSaveable { mutableStateOf(true) }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            item {
                Button(onClick = {
                    navigator.pop()
                }){
                    Text("Back")
                }

                Divider()

                Text(userData.user, fontSize = 24.sp)

                Divider()

                Button(onClick = {
                    navigator.push(ChartsScreen(userData))
                }){
                    Text("Charts")
                }

                Divider()

                Row {
                    Button(onClick = {
                        comments = userData.commented
                        myComments = true
                    }){
                        Text("(${userData.commented.size}) Comments wrote")
                    }

                    Divider()

                    Button(onClick = {
                        comments = userData.receivedComments
                        myComments = false
                    }){
                        Text("(${userData.receivedComments.size}) Comments received")
                    }
                }
            }

            items(comments){
                Comments(it, myComments)
            }
        }
    }

}


