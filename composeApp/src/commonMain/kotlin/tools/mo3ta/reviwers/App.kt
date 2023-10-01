import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import tools.mo3ta.reviwers.screens.ConfigScreen
import tools.mo3ta.reviwers.screens.PullsScreen
import tools.mo3ta.reviwers.theme.AppTheme

@Composable
internal fun App(
    systemAppearance: (isLight: Boolean) -> Unit = {}
) = AppTheme(systemAppearance) {

    Column(modifier = Modifier.fillMaxSize()) {
        Navigator(
            ConfigScreen
        )
    }
}



