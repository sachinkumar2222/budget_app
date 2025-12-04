package com.example.budgettracker.ui.components.charts

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue

import com.example.budgettracker.ui.theme.GlassyBackground
import com.example.budgettracker.ui.theme.GlassyBorder
import com.example.budgettracker.ui.theme.TextGreyLight
import com.example.budgettracker.ui.theme.TextWhite

// Data class with Color property
data class BarChartData(
    val label: String,
    val value: Double,
    val color: Color
)

@Composable
fun CustomBarChart(
    title: String,
    data: List<BarChartData>,
    modifier: Modifier = Modifier
) {
    // 1. Calculate Max Value safely
    val maxValue = remember(data) {
        (data.maxOfOrNull { it.value.absoluteValue }?.toFloat() ?: 0f) * 1.1f
    }
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    // Animation State
    val barAnimation = remember { Animatable(0f) }
    LaunchedEffect(data) {
        barAnimation.snapTo(0f)
        barAnimation.animateTo(1f, animationSpec = tween(1000, easing = LinearOutSlowInEasing))
    }

    // 2. Configuration for Bar Sizing
    val barWidthDp = 28.dp // Slightly wider for better look
    val spacingDp = 20.dp
    val itemWidthDp = barWidthDp + spacingDp

    // Dynamic width: Ensures scrolling if many items, or fits if few
    val chartWidth = (itemWidthDp * data.size).coerceAtLeast(300.dp)

    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.parseColor("#FFB0B0B0") // TextGreyLight
            textSize = density.run { 11.sp.toPx() }
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
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
                .height(220.dp) // Height of the chart area
        ) {
            // --- Y-AXIS LABELS (Fixed on Left) ---
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                val yLabelCount = 5
                val yStep = maxValue / (yLabelCount - 1)

                for (i in (yLabelCount - 1) downTo 0) {
                    val value = (yStep * i).toInt()
                    Text(text = formatCompactNumber(value.toDouble()), fontSize = 10.sp, color = TextGreyLight)
                }
                // Extra spacer to align 0 with the X-axis baseline
                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            // --- SCROLLABLE BARS ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(chartWidth)
                        .fillMaxHeight()
                        .padding(bottom = 24.dp) // Space for X-axis labels
                ) {
                    val canvasHeight = size.height
                    val barWidthPx = barWidthDp.toPx()
                    val spacingPx = spacingDp.toPx()

                    // Avoid divide by zero
                    val scaleY = if (maxValue == 0f) 0f else canvasHeight / maxValue

                    // Draw Grid Lines
                    val yLabelCount = 5
                    val stepHeight = canvasHeight / (yLabelCount - 1)
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

                    data.forEachIndexed { index, item ->
                        val valueFloat = item.value.toFloat().absoluteValue
                        val targetBarHeight = valueFloat * scaleY
                        val animatedBarHeight = targetBarHeight * barAnimation.value

                        // X position
                        val startX = (index * (barWidthPx + spacingPx)) + (spacingPx / 2)

                        // Gradient Brush
                        val brush = Brush.verticalGradient(
                            colors = listOf(item.color, item.color.copy(alpha = 0.3f)),
                            startY = canvasHeight - animatedBarHeight,
                            endY = canvasHeight
                        )

                        // Draw Bar (Rounded Top)
                        drawRoundRect(
                            brush = brush,
                            topLeft = Offset(x = startX, y = canvasHeight - animatedBarHeight),
                            size = Size(width = barWidthPx, height = animatedBarHeight),
                            cornerRadius = CornerRadius(x = 12f, y = 12f) // Smooth rounded corners
                        )

                        // Draw X-Axis Label
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawText(
                                item.label,
                                startX + barWidthPx / 2,
                                canvasHeight + density.run { 18.dp.toPx() }, // Position below bar
                                textPaint
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper to format numbers (e.g. 1200 -> 1.2k)
fun formatCompactNumber(number: Double): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000)
        number >= 1_000 -> String.format("%.1fK", number / 1_000)
        else -> number.toInt().toString()
    }
}