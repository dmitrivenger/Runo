package com.dmitrivenger.runo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dmitrivenger.runo.ui.navigation.RunoNavGraph
import com.dmitrivenger.runo.ui.theme.RunoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RunoApplication

        setContent {
            val profile by app.userPreferences.userProfile.collectAsState(
                initial = com.dmitrivenger.runo.domain.model.UserProfile()
            )
            RunoTheme(darkTheme = profile.darkMode) {
                RunoNavGraph(app = app)
            }
        }
    }
}
