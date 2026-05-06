package com.dmitrivenger.runo.ui.analytics

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.BarChart
import com.dmitrivenger.runo.ui.components.PaceChart

enum class AnalyticsPeriod(val label: String) { WEEK("Week"), MONTH("Month"), YEAR("Year") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onBack: () -> Unit,
) {
    val runs by viewModel.runs.collectAsState()
    var period by remember { mutableStateOf(AnalyticsPeriod.WEEK) }

    val filtered = remember(runs, period) { filterRuns(runs, period) }
    val totalKm = filtered.sumOf { it.distanceKm.toDouble() }.toFloat()
    val totalRuns = filtered.size
    val avgPace = if (filtered.isNotEmpty()) filtered.map { it.averagePaceSecondsPerKm }.average().toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnalyticsPeriod.entries.forEach { p ->
                        FilterChip(
                            selected = p == period,
                            onClick = { period = p },
                            label = { Text(p.label) },
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    SummaryChip("Runs", "$totalRuns")
                    SummaryChip("Distance", "%.1f km".format(totalKm))
                    SummaryChip("Avg Pace", formatPace(avgPace))
                }
            }

            if (filtered.isNotEmpty()) {
                item {
                    ChartSection(
                        title = "Distance (km)",
                        values = filtered.map { it.distanceKm }.reversed(),
                        chartColor = MaterialTheme.colorScheme.primary,
                        chartType = "bar",
                    )
                }
                item {
                    ChartSection(
                        title = "Average Pace (min/km)",
                        values = filtered.map { it.averagePaceSecondsPerKm / 60f }.reversed(),
                        chartColor = MaterialTheme.colorScheme.secondary,
                        chartType = "line",
                    )
                }
            } else {
                item {
                    Box(
                        Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("No runs in this period.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ChartSection(
    title: String,
    values: List<Float>,
    chartColor: androidx.compose.ui.graphics.Color,
    chartType: String,
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            if (chartType == "bar") {
                BarChart(values = values, barColor = chartColor)
            } else {
                PaceChart(values = values, lineColor = chartColor)
            }
        }
    }
}

private fun filterRuns(runs: List<Run>, period: AnalyticsPeriod): List<Run> {
    val now = System.currentTimeMillis()
    val cutoff = when (period) {
        AnalyticsPeriod.WEEK -> now - 7L * 24 * 3600 * 1000
        AnalyticsPeriod.MONTH -> now - 30L * 24 * 3600 * 1000
        AnalyticsPeriod.YEAR -> now - 365L * 24 * 3600 * 1000
    }
    return runs.filter { it.startTime >= cutoff }
}

private fun formatPace(paceSeconds: Float): String {
    if (paceSeconds <= 0f) return "--:--"
    val m = (paceSeconds / 60).toInt()
    val s = (paceSeconds % 60).toInt()
    return "%d:%02d".format(m, s)
}
