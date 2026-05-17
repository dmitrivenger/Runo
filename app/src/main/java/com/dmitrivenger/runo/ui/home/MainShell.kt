package com.dmitrivenger.runo.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
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
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            onStartRun()
        } else {
            showRationale = true
        }
    }

    fun handleStartRunFromNav() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
        if (granted) {
            onStartRun()
        } else {
            val perms = buildList {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    add(Manifest.permission.POST_NOTIFICATIONS)
            }.toTypedArray()
            permissionLauncher.launch(perms)
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Location needed") },
            text = { Text("Runo needs location access to track your run. Please grant it in your device Settings.") },
            confirmButton = {
                TextButton(onClick = { showRationale = false }) { Text("OK") }
            },
        )
    }

    Scaffold(
        bottomBar = {
            RunoBottomNav(
                currentTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onRunClick = { handleStartRunFromNav() },
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
                    onDeleteRun = { homeViewModel.deleteRun(it) },
                )
                BottomNavTab.STATS -> AnalyticsScreen(
                    viewModel = analyticsViewModel,
                    onBack = { selectedTab = BottomNavTab.HOME },
                )
                BottomNavTab.ACTIVITY -> HistoryScreen(
                    runs = runs,
                    onRunClick = onRunClick,
                    onDeleteRun = { homeViewModel.deleteRun(it) },
                )
                BottomNavTab.PROFILE -> ProfileScreen(
                    preferences = app.userPreferences,
                    onOpenSettings = onOpenSettings,
                )
            }
        }
    }
}
