import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import tools.mo3ta.reviwers.screens.ConfigScreen
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



