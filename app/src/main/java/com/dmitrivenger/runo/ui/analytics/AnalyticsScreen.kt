package com.dmitrivenger.runo.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.PaceChart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class AnalyticsPeriod(val label: String) { WEEK("Week"), MONTH("Month"), YEAR("Year") }

private data class ChartBar(val label: String, val value: Float)

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onBack: () -> Unit,
) {
    val runs by viewModel.runs.collectAsState()
    var period by remember { mutableStateOf(AnalyticsPeriod.WEEK) }

    val filtered = remember(runs, period) { filterRuns(runs, period) }
    val barData  = remember(runs, period) { buildBarData(runs, period) }
    val totalKm  = filtered.sumOf { it.distanceKm.toDouble() }.toFloat()
    val totalRuns = filtered.size
    val avgPace = if (filtered.isNotEmpty())
        filtered.map { it.averagePaceSecondsPerKm }.average().toFloat() else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(28.dp))
                Text(
                    text = "Analytics",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(20.dp))
                PeriodChips(selected = period, onSelect = { period = it })
                Spacer(Modifier.height(20.dp))
            }
        }

        // ── Hero stats card ───────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(20.dp),
            ) {
                Column {
                    Text(
                        text = periodLabel(period),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%.2f".format(totalKm),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.padding(start = 6.dp))
                        Text(
                            text = "km",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SubStat(
                            label = "Runs",
                            value = "$totalRuns",
                            modifier = Modifier.weight(1f),
                        )
                        SubStat(
                            label = "Avg pace",
                            value = formatPace(avgPace),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ── Distance bar chart ────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Distance",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                ) {
                    if (barData.all { it.value == 0f }) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No runs this ${period.label.lowercase()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        LabeledBarChart(
                            bars = barData,
                            barColor = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // ── Pace trend ────────────────────────────────────────────────────────
        if (filtered.size >= 2) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Pace trend",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                    ) {
                        PaceChart(
                            values = filtered.map { it.averagePaceSecondsPerKm / 60f }.reversed(),
                            lineColor = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Lower = faster pace",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // ── Empty state ───────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Text(
                        text = "No runs recorded this ${period.label.lowercase()}.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

// ── Period chips ─────────────────────────────────────────────────────────────

@Composable
private fun PeriodChips(selected: AnalyticsPeriod, onSelect: (AnalyticsPeriod) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AnalyticsPeriod.entries.forEach { period ->
            val isSelected = period == selected
            Text(
                text = period.label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                        shape = RoundedCornerShape(50),
                    )
                    .clickable { onSelect(period) }
                    .padding(horizontal = 18.dp, vertical = 8.dp),
            )
        }
    }
}

// ── Sub-stat card ─────────────────────────────────────────────────────────────

@Composable
private fun SubStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Labeled bar chart ─────────────────────────────────────────────────────────

@Composable
private fun LabeledBarChart(bars: List<ChartBar>, barColor: Color) {
    val maxValue = bars.maxOf { it.value }.coerceAtLeast(0.1f)

    Column(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val slotWidth = size.width / bars.size
            val barWidth = slotWidth * 0.55f

            bars.forEachIndexed { i, bar ->
                if (bar.value > 0f) {
                    val barH = (bar.value / maxValue) * size.height * 0.9f
                    val x = i * slotWidth + (slotWidth - barWidth) / 2f
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, size.height - barH),
                        size = Size(barWidth, barH),
                        cornerRadius = CornerRadius(6.dp.toPx()),
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Data helpers ──────────────────────────────────────────────────────────────

private fun buildBarData(runs: List<Run>, period: AnalyticsPeriod): List<ChartBar> {
    return when (period) {
        AnalyticsPeriod.WEEK -> (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            val start = cal.timeInMillis
            val end = start + 24 * 3600 * 1000L - 1
            val label = SimpleDateFormat("EE", Locale.getDefault()).format(cal.time).take(1)
            val km = runs.filter { it.startTime in start..end }.sumOf { it.distanceKm.toDouble() }.toFloat()
            ChartBar(label, km)
        }
        AnalyticsPeriod.MONTH -> (3 downTo 0).map { weeksAgo ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.WEEK_OF_YEAR, -weeksAgo)
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            val start = cal.timeInMillis
            val end = start + 7L * 24 * 3600 * 1000 - 1
            val km = runs.filter { it.startTime in start..end }.sumOf { it.distanceKm.toDouble() }.toFloat()
            ChartBar("W${4 - weeksAgo}", km)
        }
        AnalyticsPeriod.YEAR -> (11 downTo 0).map { monthsAgo ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.MONTH, -monthsAgo)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            val start = cal.timeInMillis
            val end = Calendar.getInstance().apply { timeInMillis = start; add(Calendar.MONTH, 1) }.timeInMillis - 1
            val label = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)
            val km = runs.filter { it.startTime in start..end }.sumOf { it.distanceKm.toDouble() }.toFloat()
            ChartBar(label, km)
        }
    }
}

private fun filterRuns(runs: List<Run>, period: AnalyticsPeriod): List<Run> {
    val now = System.currentTimeMillis()
    val cutoff = when (period) {
        AnalyticsPeriod.WEEK  -> now - 7L   * 24 * 3600 * 1000
        AnalyticsPeriod.MONTH -> now - 30L  * 24 * 3600 * 1000
        AnalyticsPeriod.YEAR  -> now - 365L * 24 * 3600 * 1000
    }
    return runs.filter { it.startTime >= cutoff }
}

private fun periodLabel(period: AnalyticsPeriod) = when (period) {
    AnalyticsPeriod.WEEK  -> "This week"
    AnalyticsPeriod.MONTH -> "This month"
    AnalyticsPeriod.YEAR  -> "This year"
}

private fun formatPace(paceSeconds: Float): String {
    if (paceSeconds <= 0f) return "--:--"
    return "%d:%02d".format((paceSeconds / 60).toInt(), (paceSeconds % 60).toInt())
}
