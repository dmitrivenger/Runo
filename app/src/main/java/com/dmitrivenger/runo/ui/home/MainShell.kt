package com.dmitrivenger.runo.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.ui.analytics.AnalyticsScreen
import com.dmitrivenger.runo.ui.analytics.AnalyticsViewModel
import com.dmitrivenger.runo.ui.components.BottomNavTab
import com.dmitrivenger.runo.ui.components.RunoBottomNav
import com.dmitrivenger.runo.ui.history.HistoryScreen
import com.dmitrivenger.runo.ui.profile.ProfileScreen

@Composable
fun MainShell(
    homeViewModel: HomeViewModel,
    analyticsViewModel: AnalyticsViewModel,
    app: RunoApplication,
    onStartRun: () -> Unit,
    onRunClick: (Long) -> Unit,
    onOpenSettings: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavTab.HOME) }
    val runs by homeViewModel.runs.collectAsState()

    Scaffold(
        bottomBar = {
            RunoBottomNav(
                currentTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onRunClick = onStartRun,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedTab) {
                BottomNavTab.HOME -> HomeContent(
                    viewModel = homeViewModel,
                    onStartRun = onStartRun,
                    onRunClick = onRunClick,
                )
                BottomNavTab.STATS -> AnalyticsScreen(
                    viewModel = analyticsViewModel,
                    onBack = { selectedTab = BottomNavTab.HOME },
                )
                BottomNavTab.ACTIVITY -> HistoryScreen(
                    runs = runs,
                    onRunClick = onRunClick,
                )
                BottomNavTab.PROFILE -> ProfileScreen(
                    preferences = app.userPreferences,
                    onOpenSettings = onOpenSettings,
                )
            }
        }
    }
}
