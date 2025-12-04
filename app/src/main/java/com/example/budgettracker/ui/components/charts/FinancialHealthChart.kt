package com.example.budgettracker.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgettracker.ui.theme.*
import kotlin.math.min

@Composable
fun FinancialHealthChart(
    score: Float, // 0 to 100
    maxScore: Float = 100f,
    totalIncome: Double,
    totalExpense: Double,
    totalBalance: Double,
    title: String = "Financial Health",
    modifier: Modifier = Modifier
) {
    val animatedScore = remember { Animatable(0f) }

    LaunchedEffect(score) {
        animatedScore.animateTo(
            targetValue = score,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassyBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = TextWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val radius = min(width, height * 2) / 2 - 20.dp.toPx()
                val center = Offset(width / 2, height)

                // 1. Draw Background Track
                drawArc(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 25.dp.toPx(), cap = StrokeCap.Round)
                )

                // 2. Draw Gradient Progress Arc
                val gradientColors = listOf(NeonPink, NeonOrange, NeonMint)
                val sweepAngle = 180f * (animatedScore.value / maxScore).coerceIn(0f, 1f)

                drawArc(
                    brush = Brush.horizontalGradient(gradientColors),
                    startAngle = 180f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 25.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Center Text (Score)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Text(
                    text = "${animatedScore.value.toInt()}",
                    color = TextWhite,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getHealthStatus(animatedScore.value),
                    color = getHealthColor(animatedScore.value),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HealthStatItem(label = "Income", value = "₹${totalIncome.toInt()}", color = NeonMint)
            HealthStatItem(label = "Expense", value = "₹${totalExpense.toInt()}", color = NeonPink)
            HealthStatItem(label = "Balance", value = "₹${totalBalance.toInt()}", color = NeonCyan)
        }
    }
}

@Composable
fun HealthStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextGreyLight, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

fun getHealthStatus(score: Float): String {
    return when {
        score >= 80 -> "Excellent"
        score >= 50 -> "Good"
        score >= 30 -> "Fair"
        else -> "Critical"
    }
}

fun getHealthColor(score: Float): Color {
    return when {
        score >= 80 -> NeonMint
        score >= 50 -> NeonCyan
        score >= 30 -> NeonOrange
        else -> NeonPink
    }
}
