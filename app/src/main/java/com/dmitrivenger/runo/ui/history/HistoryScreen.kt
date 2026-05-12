package com.dmitrivenger.runo.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmitrivenger.runo.R
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MiniRoutePreview
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Light_MutedGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    runs: List<Run>,
    onRunClick: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.run_history),
            style = MaterialTheme.typography.headlineLarge,
            color = Brand_DeepGreen,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        )

        if (runs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🏃",
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.no_runs_yet),
                        style = MaterialTheme.typography.titleLarge,
                        color = Brand_DeepGreen,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.start_first_run),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Light_MutedGray,
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            ) {
                items(runs, key = { it.id }) { run ->
                    HistoryRunCard(run = run, onClick = { onRunClick(run.id) })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun HistoryRunCard(run: Run, onClick: () -> Unit) {
    val dateStr = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())
        .format(Date(run.startTime))
    val unitKm = stringResource(R.string.unit_km)
    val unitPerKm = stringResource(R.string.unit_per_km)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = Light_MutedGray,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "%.2f $unitKm".format(run.distanceKm),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    ),
                    color = Brand_DeepGreen,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = run.formattedPace().replace(" /km", " $unitPerKm"),
                    style = MaterialTheme.typography.labelLarge,
                    color = Brand_DeepGreen,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = run.formattedDuration(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Light_MutedGray,
                )
            }
            Spacer(Modifier.width(12.dp))
            MiniRoutePreview(
                points = run.routePoints,
                modifier = Modifier
                    .width(80.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
    }
}

