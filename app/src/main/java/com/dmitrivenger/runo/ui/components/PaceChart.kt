package com.dmitrivenger.runo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.ui.theme.Light_BackgroundCream

@Composable
fun MiniRoutePreview(points: List<LatLng>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Light_BackgroundCream)) {
        if (points.size < 2) return@Canvas

        val pad = 8.dp.toPx()
        val drawW = size.width - pad * 2
        val drawH = size.height - pad * 2

        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLng = points.minOf { it.longitude }
        val maxLng = points.maxOf { it.longitude }

        val latRange = (maxLat - minLat).coerceAtLeast(0.0001)
        val lngRange = (maxLng - minLng).coerceAtLeast(0.0001)

        val scale = minOf(drawW / lngRange, drawH / latRange).toFloat()
        val projW = (lngRange * scale).toFloat()
        val projH = (latRange * scale).toFloat()
        val offX = pad + (drawW - projW) / 2f
        val offY = pad + (drawH - projH) / 2f

        fun px(lng: Double) = offX + ((lng - minLng) * scale).toFloat()
        fun py(lat: Double) = offY + projH - ((lat - minLat) * scale).toFloat()

        val path = Path().apply {
            moveTo(px(points[0].longitude), py(points[0].latitude))
            for (i in 1 until points.size) {
                lineTo(px(points[i].longitude), py(points[i].latitude))
            }
        }
        drawPath(
            path = path,
            color = Color(0xFF0F3D2E),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawCircle(
            color = Color(0xFF5C9A4A),
            radius = 3.5.dp.toPx(),
            center = Offset(px(points.first().longitude), py(points.first().latitude)),
        )
        drawCircle(
            color = Color(0xFFC0392B),
            radius = 3.5.dp.toPx(),
            center = Offset(px(points.last().longitude), py(points.last().latitude)),
        )
    }
}

@Composable
fun PaceChart(
    values: List<Float>,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
) {
    if (values.size < 2) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val max = values.max()
        val min = values.min()
        val range = (max - min).coerceAtLeast(1f)
        val stepX = size.width / (values.size - 1)

        val path = Path()
        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - min) / range) * size.height * 0.8f - size.height * 0.1f
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            )
        )

        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - min) / range) * size.height * 0.8f - size.height * 0.1f
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
        }
    }
}

@Composable
fun BarChart(
    values: List<Float>,
    barColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
) {
    if (values.isEmpty()) return
    val max = values.max().coerceAtLeast(1f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = (size.width / values.size) * 0.6f
        val gap = size.width / values.size

        values.forEachIndexed { index, value ->
            val barHeight = (value / max) * size.height * 0.85f
            val x = index * gap + (gap - barWidth) / 2f
            val y = size.height - barHeight

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            )
        }
    }
}
