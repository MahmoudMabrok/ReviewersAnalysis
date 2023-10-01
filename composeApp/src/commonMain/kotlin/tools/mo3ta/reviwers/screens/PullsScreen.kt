package tools.mo3ta.reviwers.screens

import tools.mo3ta.reviwers.viewmodel.PullsViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import tools.mo3ta.reviwers.components.SortGroup
import tools.mo3ta.reviwers.components.UserItem
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import tools.mo3ta.reviwers.components.Loading

data class PullsScreenData(val apiKey:String, val ownerWithRepo:String, val isEnterprise:Boolean, val enterprise:String)


data class PullsScreen(val data: PullsScreenData) : Screen {
    @Composable
    override fun Content() {
        val viewModel = getViewModel(Unit, viewModelFactory { PullsViewModel(data) })
        val uiState by viewModel.uiState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow


        if (uiState.isLoading){
            Loading(Modifier.fillMaxSize(), uiState.currentPage)
        }else{
            Column(
                Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Button(onClick = {
                    navigator.popUntil {
                        it == ConfigScreen
                    }
                }){
                    Text("Return to Settings")
                }

                if (uiState.isNextShown){
                    Button(onClick = {
                        viewModel.getPulls()
                    }){
                        Text("Next Page ${ uiState.currentPage}")
                    }
                }
                Text(text= "Number of pulls: ${uiState.pulls}", fontSize = 24.sp)
                Spacer(modifier = Modifier.size(8.dp))
                SortGroup(uiState.sortTypes){
                    viewModel.onChangeFilter(it)
                }
                if (uiState.data.isEmpty()){
                    Text("No Approval or comments done ${uiState.data.size}")
                }

                AnimatedVisibility(uiState.data.isNotEmpty()) {
                    Column(
                        //  columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        content = {
                            uiState.data.map {
                                Box(modifier = Modifier.padding(bottom = 8.dp, end = 8.dp).clickable {
                                    navigator.push(UserDetails(it))
                                }){
                                    UserItem(it)
                                }
                            }
//                    items(uiState.data) {
//                        Box(modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)){
//                            UserItem(it)
//                        }
//                    }
                        }
                    )
                }

            }
        }
    }

}
