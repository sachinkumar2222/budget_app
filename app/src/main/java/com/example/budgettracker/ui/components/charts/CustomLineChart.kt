package com.example.budgettracker.ui.components.charts

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgettracker.ui.theme.GlassyBackground
import com.example.budgettracker.ui.theme.GlassyBorder
import com.example.budgettracker.ui.theme.TextGreyLight
import com.example.budgettracker.ui.theme.TextWhite
import kotlin.math.absoluteValue

data class LineChartData(
    val label: String,
    val value: Double
)

@Composable
fun CustomLineChart(
    title: String,
    data: List<LineChartData>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF875CF5),
    fillColor: Color = Color(0xFF875CF5).copy(alpha = 0.2f)
) {
    if (data.isEmpty()) return

    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    // Animation for path drawing
    val pathProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        pathProgress.snapTo(0f)
        pathProgress.animateTo(1f, animationSpec = tween(1500, easing = LinearEasing))
        // Scroll to the max value to show the most recent data (Right side)
        scrollState.scrollTo(scrollState.maxValue)
    }

    // Calculate spacing to show roughly 5 items on screen
    // 60-70dp is usually good for mobile screens
    val pointSpacing = 70.dp
    val chartWidth = (pointSpacing * (data.size - 1).coerceAtLeast(1)) + 60.dp

    val maxY = remember(data) {
        (data.maxOfOrNull { it.value.absoluteValue }?.toFloat() ?: 0f) * 1.2f
    }

    val labelPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.parseColor("#B3FFFFFF") // TextGreyLight equivalent
            textAlign = Paint.Align.CENTER
            textSize = density.run { 10.sp.toPx() }
            typeface = Typeface.DEFAULT
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GlassyBackground, RoundedCornerShape(24.dp)) // Glassy Background
            .border(BorderStroke(1.dp, GlassyBorder), RoundedCornerShape(24.dp)) // Glassy Border
            .padding(20.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Spacer(modifier = Modifier.height(24.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Fixed Y-Axis
            Column(
                modifier = Modifier.width(40.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                val yLabelCount = 5
                val yStep = maxY / (yLabelCount - 1)
                for (i in (yLabelCount - 1) downTo 0) {
                    val value = (yStep * i).toInt()
                    Text(text = formatCompactNumber(value.toDouble()), fontSize = 10.sp, color = TextGreyLight)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Scrollable Chart
            Box(modifier = Modifier.weight(1f).horizontalScroll(scrollState)) {
                Canvas(
                    modifier = Modifier.width(chartWidth).fillMaxHeight().padding(bottom = 20.dp, top = 10.dp)
                ) {
                    val height = size.height
                    val spacingPx = pointSpacing.toPx()
                    val scaleY = if (maxY == 0f) 0f else height / maxY

                    // Draw Grid Lines
                    val yLabelCount = 5
                    val stepHeight = height / (yLabelCount - 1)
                    for (i in 0 until yLabelCount) {
                        val y = stepHeight * i
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    val points = data.mapIndexed { index, item ->
                        val x = index * spacingPx + 10.dp.toPx()
                        val y = height - (item.value.toFloat().absoluteValue * scaleY)
                        Offset(x, y)
                    }

                    val path = Path()
                    if (points.isNotEmpty()) {
                        path.moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val cp1X = (p1.x + p2.x) / 2
                            val cp1Y = p1.y
                            val cp2X = (p1.x + p2.x) / 2
                            val cp2Y = p2.y
                            path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2.x, p2.y)
                        }
                    }

                    // Draw Fill Gradient
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(points.last().x, height)
                        lineTo(points.first().x, height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(fillColor, Color.Transparent),
                            startY = 0f, endY = height
                        ),
                        alpha = pathProgress.value // Fade in fill
                    )

                    // Draw Line with Glow
                    // Glow layer (thicker, transparent)
                    drawPath(
                        path = path,
                        color = lineColor.copy(alpha = 0.4f),
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    // Main line
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw Points and Labels
                    points.forEachIndexed { index, point ->
                        if (pathProgress.value > 0.9f) { // Show points at end of animation
                            drawCircle(color = TextWhite, radius = 6.dp.toPx(), center = point)
                            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)
                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawText(data[index].label, point.x, height + 15.dp.toPx(), labelPaint)
                            }
                        }
                    }
                }
            }
        }
    }
}