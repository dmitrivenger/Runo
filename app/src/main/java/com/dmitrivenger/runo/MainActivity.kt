package com.dmitrivenger.runo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dmitrivenger.runo.ui.navigation.RunoNavGraph
import com.dmitrivenger.runo.ui.navigation.Screen
import com.dmitrivenger.runo.ui.theme.RunoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RunoApplication

        val isOnboardingDone = runBlocking { app.userPreferences.isOnboardingDone.first() }
        val startDestination = if (isOnboardingDone) Screen.Home.route else Screen.Welcome.route

        setContent {
            val profile by app.userPreferences.userProfile.collectAsState(
                initial = com.dmitrivenger.runo.domain.model.UserProfile()
            )

            RunoTheme(darkTheme = profile.darkMode) {
                RunoNavGraph(app = app, startDestination = startDestination)
            }
        }
    }
}
