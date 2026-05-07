package com.dmitrivenger.runo.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.ui.analytics.AnalyticsScreen
import com.dmitrivenger.runo.ui.analytics.AnalyticsViewModel
import com.dmitrivenger.runo.ui.components.RunoBottomNav
import com.dmitrivenger.runo.ui.components.RunoTab
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
    var selectedTab by rememberSaveable { mutableStateOf(RunoTab.HOME) }

    Scaffold(
        bottomBar = {
            RunoBottomNav(
                selected = selectedTab,
                onSelect = { selectedTab = it },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedTab) {
                RunoTab.HOME -> HomeContent(
                    viewModel = homeViewModel,
                    onStartRun = onStartRun,
                    onRunClick = onRunClick,
                )
                RunoTab.ANALYTICS -> AnalyticsScreen(
                    viewModel = analyticsViewModel,
                    onBack = { selectedTab = RunoTab.HOME },
                )
                RunoTab.PROFILE -> ProfileScreen(
                    preferences = app.userPreferences,
                    onOpenSettings = onOpenSettings,
                )
            }
        }
    }
}
