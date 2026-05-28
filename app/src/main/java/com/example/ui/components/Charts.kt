package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun SimpleLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (dataPoints.isEmpty()) return

    val maxData = dataPoints.maxOrNull() ?: 1f
    val minData = dataPoints.minOrNull() ?: 0f
    
    val range = if (maxData == minData) 1f else (maxData - minData)

    Canvas(modifier = modifier.fillMaxWidth().height(200.dp).padding(16.dp)) {
        val width = size.width
        val height = size.height
        
        val startX = 0f
        val stepX = width / (dataPoints.size.coerceAtLeast(2) - 1).coerceAtLeast(1)

        val path = Path()

        dataPoints.forEachIndexed { index, data ->
            val normalizedData = (data - minData) / range
            val x = startX + index * stepX
            val y = height - (normalizedData * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw points
        dataPoints.forEachIndexed { index, data ->
            val normalizedData = (data - minData) / range
            val x = startX + index * stepX
            val y = height - (normalizedData * height)
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
