package tools.mo3ta.reviwers

import App
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemBarColor = Color.TRANSPARENT
        setContent {
            val view = LocalView.current
            var isLightStatusBars by remember { mutableStateOf(false) }
            if (!view.isInEditMode) {
                LaunchedEffect(isLightStatusBars) {
                    val window = (view.context as Activity).window
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.statusBarColor = systemBarColor
                    window.navigationBarColor = systemBarColor
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = isLightStatusBars
                        isAppearanceLightNavigationBars = isLightStatusBars
                    }
                }
            }
            App(systemAppearance = { isLight -> isLightStatusBars = isLight })
        }
    }
}

internal actual fun openUrl(url: String?) {
    val uri = url?.let { Uri.parse(it) } ?: return
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AndroidApp.INSTANCE.startActivity(intent)
}

@Composable
@Preview
fun tttt() {
    Card (modifier = Modifier.fillMaxWidth(0.5f), colors = CardDefaults.cardColors(
        containerColor = androidx.compose.ui.graphics.Color.White
    )){
        Column (Modifier.padding(16.dp)){
            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                Text("2024")
                Text("Wedensay")
            }
            Spacer(Modifier.size(8.dp))
            Text("Opened: 12", color = androidx.compose.ui.graphics.Color.Green)
            Text("Merged: 12", color = androidx.compose.ui.graphics.Color.Magenta)
        }
    }
}