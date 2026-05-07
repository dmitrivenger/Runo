package com.dmitrivenger.runo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.ui.analytics.AnalyticsScreen
import com.dmitrivenger.runo.ui.analytics.AnalyticsViewModel
import com.dmitrivenger.runo.ui.analytics.AnalyticsViewModelFactory
import com.dmitrivenger.runo.ui.history.RunDetailScreen
import com.dmitrivenger.runo.ui.home.HomeViewModel
import com.dmitrivenger.runo.ui.home.HomeViewModelFactory
import com.dmitrivenger.runo.ui.home.MainShell
import com.dmitrivenger.runo.ui.onboarding.OnboardingScreen
import com.dmitrivenger.runo.ui.onboarding.WelcomeScreen
import com.dmitrivenger.runo.ui.run.ActiveRunScreen
import com.dmitrivenger.runo.ui.run.ActiveRunViewModel
import com.dmitrivenger.runo.ui.run.CountdownScreen
import com.dmitrivenger.runo.ui.settings.SettingsScreen
import com.dmitrivenger.runo.ui.splash.SplashScreen
import com.dmitrivenger.runo.ui.summary.SummaryScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Countdown : Screen("countdown")
    object ActiveRun : Screen("active_run")
    object Summary : Screen("summary/{runId}") {
        fun createRoute(runId: Long) = "summary/$runId"
    }
    object RunDetail : Screen("run_detail/{runId}") {
        fun createRoute(runId: Long) = "run_detail/$runId"
    }
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
}

@Composable
fun RunoNavGraph(app: RunoApplication) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(onComplete = {
                val isOnboardingDone = runBlocking { app.userPreferences.isOnboardingDone.first() }
                val dest = if (isOnboardingDone) Screen.Home.route else Screen.Welcome.route
                navController.navigate(dest) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(onGetStarted = { navController.navigate(Screen.Onboarding.route) })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = { profile ->
                runBlocking {
                    app.userPreferences.saveProfile(profile)
                    app.userPreferences.setOnboardingDone()
                }
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            val homeVm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(app.runRepository, app.userPreferences)
            )
            val analyticsVm: AnalyticsViewModel = viewModel(
                factory = AnalyticsViewModelFactory(app.runRepository)
            )
            MainShell(
                homeViewModel = homeVm,
                analyticsViewModel = analyticsVm,
                app = app,
                onStartRun = { navController.navigate(Screen.Countdown.route) },
                onRunClick = { id -> navController.navigate(Screen.RunDetail.createRoute(id)) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
            )
        }

        composable(Screen.Countdown.route) {
            CountdownScreen(onCountdownComplete = {
                navController.navigate(Screen.ActiveRun.route) {
                    popUpTo(Screen.Countdown.route) { inclusive = true }
                }
            })
        }

        composable(Screen.ActiveRun.route) {
            val vm: ActiveRunViewModel = viewModel()
            val profile by app.userPreferences.userProfile.collectAsState(
                initial = com.dmitrivenger.runo.domain.model.UserProfile()
            )
            ActiveRunScreen(
                viewModel = vm,
                userProfile = profile,
                onRunFinished = { runId ->
                    navController.navigate(Screen.Summary.createRoute(runId)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
            )
        }

        composable(
            route = Screen.Summary.route,
            arguments = listOf(navArgument("runId") { type = NavType.LongType }),
        ) { backStack ->
            val runId = backStack.arguments!!.getLong("runId")
            SummaryScreen(
                runId = runId,
                app = app,
                onDone = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Screen.RunDetail.route,
            arguments = listOf(navArgument("runId") { type = NavType.LongType }),
        ) { backStack ->
            val runId = backStack.arguments!!.getLong("runId")
            RunDetailScreen(runId = runId, app = app, onBack = { navController.popBackStack() })
        }

        composable(Screen.Analytics.route) {
            val vm: AnalyticsViewModel = viewModel(
                factory = AnalyticsViewModelFactory(app.runRepository)
            )
            AnalyticsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(preferences = app.userPreferences, onBack = { navController.popBackStack() })
        }
    }
}
