package tools.mo3ta.reviwers.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import tools.mo3ta.reviwers.components.Comments
import tools.mo3ta.reviwers.components.Divider
import tools.mo3ta.reviwers.viewmodel.UserContribution

data class UserDetails(val userData: UserContribution ) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var comments by rememberSaveable { mutableStateOf(userData.commented) }
        var isMyCommentsSelected by rememberSaveable { mutableStateOf(true) }

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
                    Text(
                        modifier = Modifier
                            .clickable {
                            comments = userData.commented
                            isMyCommentsSelected = true
                        } .padding(16.dp),
                        text = "(${userData.commented.size}) Comments wrote",
                        textDecoration = if (isMyCommentsSelected) TextDecoration.Underline else TextDecoration.None )

                    Divider()

                    Text(
                        modifier = Modifier.clickable {
                            comments = userData.receivedComments
                            isMyCommentsSelected = false
                        } .padding(16.dp),
                        text ="(${userData.receivedComments.size}) Comments received",
                        textDecoration = if (isMyCommentsSelected) TextDecoration.None else TextDecoration.Underline )

                }
            }

            items(comments){
                Comments(it, isMyCommentsSelected)
            }
        }
    }

}


