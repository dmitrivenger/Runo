package com.dmitrivenger.runo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.ui.analytics.AnalyticsScreen
import com.dmitrivenger.runo.ui.analytics.AnalyticsViewModel
import com.dmitrivenger.runo.ui.components.BottomNavTab
import com.dmitrivenger.runo.ui.components.RunoBottomNav
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
                BottomNavTab.ACTIVITY -> ActivityPlaceholder()
                BottomNavTab.PROFILE -> ProfileScreen(
                    preferences = app.userPreferences,
                    onOpenSettings = onOpenSettings,
                )
            }
        }
    }
}

@Composable
private fun ActivityPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Run history — coming soon",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
