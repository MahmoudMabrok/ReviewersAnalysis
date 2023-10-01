package tools.mo3ta.reviwers.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.russhwolf.settings.Settings
import tools.mo3ta.reviwers.components.LabeledContent


object Keys {
    const val API_KEY = "API_KEY";
    const val OWNER_REPO = "OWNER_REPO";
    const val IS_ENTERPRISE = "IS_ENTERPRISE";
    const val ENTERPRISE = "ENTERPRISE";
}


class HomeScreenModel : ScreenModel {


}

object ConfigScreen : Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxSize().padding(16.dp).background(
                Color(0XFFAAFF)
            )
        ) {

            val settings = Settings()

            var githubKey by rememberSaveable { mutableStateOf(settings.getString(Keys.API_KEY, ""))}
            var ownerWithRepo by rememberSaveable { mutableStateOf(settings.getString(Keys.OWNER_REPO, ""))}
            var isEnterprise by rememberSaveable { mutableStateOf(settings.getBoolean(Keys.IS_ENTERPRISE, false))}
            var enterprise by rememberSaveable { mutableStateOf(settings.getString(Keys.ENTERPRISE, ""))}

            Text("Config data:", fontSize = 32.sp)
            LabeledContent("github api key"){
                TextField(value = githubKey , singleLine = true, onValueChange = {value -> githubKey = value})
            }

            LabeledContent("user/repo:"){
                TextField(value = ownerWithRepo , onValueChange = {value -> ownerWithRepo = value})
            }
            LabeledContent("is Enterprise ?"){
                Checkbox(checked = isEnterprise, onCheckedChange = { state -> isEnterprise = state})
            }
            if (isEnterprise){
                LabeledContent("Enterprise name:"){
                    TextField(value = enterprise , singleLine = true, onValueChange = {value -> enterprise = value})
                }
            }
            Button(onClick = {
                settings.putString(Keys.API_KEY, githubKey)
                settings.putString(Keys.OWNER_REPO, ownerWithRepo)
                settings.putBoolean(Keys.IS_ENTERPRISE, isEnterprise)
                settings.putString(Keys.ENTERPRISE, enterprise)

                navigator.push(PullsScreen(PullsScreenData(githubKey, ownerWithRepo, isEnterprise, enterprise)))
            }, modifier = Modifier.align(Alignment.CenterHorizontally)){
                Text("Fire")
            }

        }
    }

}

