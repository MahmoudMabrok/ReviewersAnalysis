import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.uikit.OnFocusBehavior
import platform.UIKit.UIViewController

fun MainViewController(systemAppearance: (isLight: Boolean) -> Unit): UIViewController {
    return ComposeUIViewController(
        configure = {
            //Required for WindowInsets behaviour.
            //Analog of Android Manifest activity.windowSoftInputMode="adjustNothing"
            onFocusBehavior = OnFocusBehavior.DoNothing
        }
    ) { App(systemAppearance) }
}
