package com.dmitrivenger.runo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.ui.components.RunTile
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartRun: () -> Unit,
    onRunClick: (Long) -> Unit,
    onAnalytics: () -> Unit,
    onSettings: () -> Unit,
) {
    val profile by viewModel.profile.collectAsState()
    val runs by viewModel.runs.collectAsState()

    val greeting = buildGreeting(profile.name)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onStartRun,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(72.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Start Run", modifier = Modifier.size(32.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = if (profile.name.isNotBlank()) profile.name else "Runner",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Row {
                            IconButton(onClick = onAnalytics) {
                                Icon(Icons.Default.BarChart, "Analytics",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = onSettings) {
                                Icon(Icons.Default.Settings, "Settings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    if (runs.isNotEmpty()) {
                        val totalKm = runs.sumOf { it.distanceKm.toDouble() }
                        val totalRuns = runs.size
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            SummaryItem("Total runs", "$totalRuns")
                            SummaryItem("Total km", "%.1f".format(totalKm))
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    Text(
                        text = if (runs.isEmpty()) "Ready to run?" else "Recent runs",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (runs.isEmpty()) "Tap the + button to start your first session."
                        else "${runs.size} session${if (runs.size != 1) "s" else ""} logged",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            items(runs.take(20)) { run ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    RunTile(run = run, onClick = { onRunClick(run.id) })
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun buildGreeting(name: String): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}
