package tools.mo3ta.reviwers.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import tools.mo3ta.reviwers.model.UserReviews
import tools.mo3ta.reviwers.utils.formatDate
import tools.mo3ta.reviwers.viewmodel.APPROVE
import tools.mo3ta.reviwers.viewmodel.COMMENETS
import tools.mo3ta.reviwers.viewmodel.COMMENETS_RECEIVED
import tools.mo3ta.reviwers.viewmodel.UserContribution


data class ChartsScreen(val userData: UserContribution ) : Screen {


    fun getListByState(labels:List<String> , data: Map<String, List<UserReviews>> , selection:String): List<Double> {
        return labels.map { data[it]?.filter { review ->  review.state == selection }?.size?.toDouble() ?: 0.0 }
    }

    @Composable
    override fun Content() {
        val allData = buildList {
            addAll(userData.approved)
            addAll(userData.commented)
            addAll(userData.receivedComments)
        }

        val groups = allData.groupBy { formatDate(it.date ?: "") }
        val labels by rememberSaveable { mutableStateOf(groups.keys.filterNot { it.isEmpty() }) }
        val approvals by rememberSaveable { mutableStateOf(getListByState(labels, groups, APPROVE)) }
        val comments by rememberSaveable { mutableStateOf(getListByState(labels, groups, COMMENETS)) }
        val received by rememberSaveable { mutableStateOf(getListByState(labels, groups, COMMENETS_RECEIVED)) }
        val barRange = listOf(approvals.average(), comments.average(), received.average()).max().toInt()

        val testBarParameters: List<BarParameters> = listOf(
            BarParameters(
                dataName = "Approvals",
                data = approvals,
                barColor = Color(0xFF00704F)
            ),
            BarParameters(
                dataName = "Comments",
                data = comments,
                barColor = Color(0xffeea83e),
            ),
            BarParameters(
                dataName = "Comments Received",
                data = received,
                barColor = Color(0xFFFF0000),
            ),
        )

        val navigator = LocalNavigator.currentOrThrow

        val testLineParameters: List<LineParameters> = listOf(
            LineParameters(
                label = "coverage",
                data = userData.quality.map { it.quality.toDouble() },
                lineColor = Color.Gray,
                lineType = LineType.DEFAULT_LINE,
                lineShadow = true,
            ),
        )

        Column (
            modifier = Modifier.fillMaxSize().padding(32.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally){
            Button(onClick = {
                navigator.pop()
            }){
                Text("Back")
            }

            Box(Modifier.fillMaxWidth().height(500.dp).padding(16.dp)) {
                BarChart(
                    chartParameters = testBarParameters,
                    gridColor = Color.Black,
                    xAxisData = labels,
                    isShowGrid = true,
                    animateChart = true,
                    showGridWithSpacer = true,
                    yAxisStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                    ),
                    xAxisStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.W400
                    ),
                    yAxisRange = barRange,
                    barWidth = 20.dp
                )
            }

            Divider()



           if (userData.quality.isNotEmpty()){
               Text("Coverage")

               Divider()

               Box(Modifier.fillMaxWidth().height(400.dp).padding(16.dp)) {
                   LineChart(
                       modifier = Modifier.fillMaxWidth(),
                       linesParameters = testLineParameters,
                       isGrid = true,
                       gridColor = Color.Blue,
                       xAxisData = List(testLineParameters.first().data.size){""},
                       showXAxis = false,
                       showYAxis = false,
                       animateChart = true,
                       showGridWithSpacer = true,
                       yAxisStyle = TextStyle(
                           fontSize = 14.sp,
                           color = Color.Gray,
                       ),
                       xAxisStyle = TextStyle(
                           fontSize = 14.sp,
                           color = Color.Gray,
                           fontWeight = FontWeight.W400
                       ),
                       yAxisRange = userData.quality.maxOfOrNull { it.quality }?.toInt() ?: 100,
                       gridOrientation = GridOrientation.VERTICAL,
                   )
               }
           }

        }


    }

}