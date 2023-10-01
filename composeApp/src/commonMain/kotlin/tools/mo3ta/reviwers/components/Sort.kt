package tools.mo3ta.reviwers.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tools.mo3ta.reviwers.viewmodel.SortTypes


@Composable
fun SortGroup(sortTypes: SortTypes, onClick: (SortTypes) -> Unit ) {
    Column(
        horizontalAlignment = Alignment.Start ,
        modifier = Modifier.fillMaxWidth()
    ) {
        LabelRadio(sortTypes.name == SortTypes.APPROVALS.name, "Sort by Approval"){
            onClick(SortTypes.APPROVALS)
        }
        LabelRadio(sortTypes.name == SortTypes.COMMENTS.name, "Sort by Comments"){
            onClick(SortTypes.COMMENTS)
        }
        LabelRadio(sortTypes.name == SortTypes.REVIEWED.name, "Sort by Received comments"){
            onClick(SortTypes.REVIEWED)
        }
        LabelRadio(sortTypes.name == SortTypes.QUALITY.name, "Sort by Quality"){
            onClick(SortTypes.QUALITY)
        }
    }
}



@Composable
fun LabelRadio(isSelected:Boolean , text:String, onClick : ()-> Unit) {

    Row (verticalAlignment = Alignment.CenterVertically){
        Text(text = text)
        RadioButton(selected = isSelected, onClick)
    }
}