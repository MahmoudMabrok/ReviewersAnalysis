package tools.mo3ta.reviwers.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import tools.mo3ta.reviwers.components.Loading
import tools.mo3ta.reviwers.components.SortGroup
import tools.mo3ta.reviwers.components.UserItem
import tools.mo3ta.reviwers.viewmodel.PullsViewModel

data class PullsData(val apiKey:String,
                     val ownerWithRepo:String,
                     val isEnterprise:Boolean,
                     val enterprise:String,
                     val pageSize: Int = 10,
                     val lastPageNumber: Int = 1 )


data class PRReviewAnalysisScreen(val data: PullsData) : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel = getViewModel(Unit, viewModelFactory { PullsViewModel(data) })
        val uiState by viewModel.uiState.collectAsState()
        var query by rememberSaveable { mutableStateOf("") }

        val navigator = LocalNavigator.currentOrThrow


        if (uiState.isLoading){
            Loading(Modifier.fillMaxSize(), uiState.currentPage)
        }else{
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier =  Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                item (span = { GridItemSpan(maxLineSpan)}){
                    Button(onClick = {
                        navigator.popUntil {
                            it == ConfigScreen
                        }
                    }){
                        Text("Return to Settings")
                    }
                }

                item (span = { GridItemSpan(maxLineSpan)}){
                    if (uiState.isNextShown){
                        Button(
                            modifier = Modifier.wrapContentSize(),
                            onClick = {
                            viewModel.getPulls()
                        }){
                            Text("Next Page ${ uiState.currentPage}")
                        }
                    }
                }


                item (span = { GridItemSpan(maxLineSpan)}){
                    Text(text= "Number of pulls: ${uiState.pulls}", fontSize = 24.sp)
                    Spacer(modifier = Modifier.size(8.dp))
                }

                item {
                    SortGroup(uiState.sortTypes){
                        viewModel.onChangeFilter(it)
                    }
                }

                item {
                    OutlinedTextField(
                        value = query ,
                        onValueChange = {
                        query = it
                        viewModel.onSearchQuery(query)
                    }, shape = RoundedCornerShape(16.dp),
                        label = { Text("Search by user") } ,
                        placeholder =  { Text("Type use name") } ,
                    )
                }

                item (span = { GridItemSpan(maxLineSpan)}){
                    if (uiState.data.isEmpty() && query.isBlank()){
                        Text("No Approval or comments done ${uiState.data.size}")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }

                item (span = { GridItemSpan(maxLineSpan)}){
                    if (uiState.lastDate.isNotBlank()){
                        Text("This data until : ${uiState.lastDate}")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }

                item (span = { GridItemSpan(maxLineSpan)}){
                    Spacer(modifier = Modifier.size(8.dp))
                }

                items(uiState.data, key = { it.user}){
                    Box(modifier = Modifier.animateItemPlacement().padding(bottom = 8.dp, end = 8.dp).clickable {
                        navigator.push(UserDetails(it))
                    }){
                        UserItem(it)
                    }
                }

            }
        }
    }
}
