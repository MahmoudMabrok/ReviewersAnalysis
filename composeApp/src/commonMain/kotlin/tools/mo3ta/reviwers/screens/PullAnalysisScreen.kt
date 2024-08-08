package tools.mo3ta.reviwers.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import tools.mo3ta.reviwers.components.DayActivityItem
import tools.mo3ta.reviwers.components.Loading
import tools.mo3ta.reviwers.components.PullRequestMergeItem
import tools.mo3ta.reviwers.components.SortGroup
import tools.mo3ta.reviwers.components.UserItem
import tools.mo3ta.reviwers.viewmodel.PullsAnalysisViewModel
import tools.mo3ta.reviwers.viewmodel.PullsViewModel

data class PullAnalysisScreen(val data: PullsData) : Screen {
    @OptIn(
        ExperimentalFoundationApi::class
    )
    @Composable
    override fun Content() {
        val viewModel =
            getViewModel(
                Unit,
                viewModelFactory {
                    PullsAnalysisViewModel(
                        data
                    )
                })
        val uiState by viewModel.uiState.collectAsState()

        var isOpenDay by rememberSaveable { mutableStateOf(false ) }
        var isOpenMerge by rememberSaveable { mutableStateOf(false ) }

        var query by rememberSaveable { mutableStateOf("" ) }

        val navigator =
            LocalNavigator.currentOrThrow

        Column(
            Modifier.verticalScroll(
                rememberScrollState()
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                Loading(
                    Modifier.fillMaxSize(),
                    uiState.currentPage
                )
            } else {

                Button({
                    navigator.pop()
                }){
                    Text("Back")
                }
                Spacer(
                    Modifier.size(
                        16.dp
                    )
                )

                Text(
                    "Count: ${uiState.data.size}"
                )

                Spacer(
                    Modifier.size(
                        16.dp
                    )
                )

                Text(
                    "Day Activity Analysis",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    modifier = Modifier.fillMaxWidth()
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .border(
                            shape = RectangleShape,
                            border = BorderStroke(
                                1.dp,
                                Color.Black
                            )
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ).clickable {
                            isOpenDay = isOpenDay.not()
                        }
                )
                Spacer(
                    Modifier.size(
                        8.dp
                    )
                )


                if (isOpenDay){
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Button(onClick = { viewModel.sortActivity(true)}){
                            Text("ASC")
                        }

                        Spacer(
                            Modifier.size(
                                8.dp
                            )
                        )

                        Button(onClick = { viewModel.sortActivity(false)}){
                            Text("DSC")
                        }
                    }

                    Spacer(
                        Modifier.size(
                            8.dp
                        )
                    )
                    uiState.dayActivities.map { it ->
                        Box(modifier = Modifier.align(Alignment.CenterHorizontally)){
                            DayActivityItem(
                                it
                            )
                        }
                    }
                }

                Spacer(
                    Modifier.size(
                        16.dp
                    )
                )

                Text(
                    "Merge Time Analysis",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    modifier = Modifier.fillMaxWidth()
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .border(
                            shape = RectangleShape,
                            border = BorderStroke(
                                1.dp,
                                Color.Black
                            )
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ).clickable {
                            isOpenMerge = isOpenMerge.not()
                        }
                )
                Spacer(
                    Modifier.size(
                        8.dp
                    )
                )

               if (isOpenMerge){

                   Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                       Button(onClick = { viewModel.sort(true)}){
                           Text("ASC")
                       }

                       Spacer(
                           Modifier.size(
                               8.dp
                           )
                       )

                       Button(onClick = { viewModel.sort(false)}){
                           Text("DSC")
                       }
                   }

                   Spacer(
                       Modifier.size(
                           8.dp
                       )
                   )

                   OutlinedTextField(
                       value = query ,
                       onValueChange = {
                           query = it
                           viewModel.onSearchQuery(query)
                       }, shape = RoundedCornerShape(16.dp),
                       label = { Text("Search by user") } ,
                       placeholder =  { Text("Type use name") } ,
                   )
                   Spacer(
                       Modifier.size(
                           8.dp
                       )
                   )

                   uiState.data.map {
                       Box(modifier = Modifier.align(Alignment.CenterHorizontally)){
                           PullRequestMergeItem(
                               it
                           )
                       }
                   }
               }

            }
        }
    }
}
