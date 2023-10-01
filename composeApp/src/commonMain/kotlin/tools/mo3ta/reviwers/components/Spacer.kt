package tools.mo3ta.reviwers.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Divider(size:Int = 16) {
    Spacer(modifier = Modifier.size(size.dp))
}