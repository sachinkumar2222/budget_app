package com.example.budgettracker.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue

import com.example.budgettracker.ui.theme.GlassyBackground
import com.example.budgettracker.ui.theme.GlassyBorder
import com.example.budgettracker.ui.theme.TextGreyLight
import com.example.budgettracker.ui.theme.TextWhite

@Immutable
data class PieChartData(
    val name: String,
    val value: Double,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomPieChart(
    title: String,
    data: List<PieChartData>,
    centerTitle: String,
    centerValue: String,
    modifier: Modifier = Modifier
) {
    val total = remember(data) { data.sumOf { it.value.absoluteValue } }

    var animationPlayed by remember { mutableStateOf(false) }
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 100, easing = LinearOutSlowInEasing),
        label = "pieAnimation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GlassyBackground, RoundedCornerShape(24.dp)) // Glassy Background
            .border(BorderStroke(1.dp, GlassyBorder), RoundedCornerShape(24.dp)) // Glassy Border
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp) // Slightly larger container
        ) {
            Canvas(
                modifier = Modifier
                    .size(180.dp)
                    .rotate(-90f)
            ) {
                val strokeWidth = 35.dp.toPx() // Thicker donut
                var startAngle = 0f

                data.forEach { slice ->
                    val sweepAngle = (slice.value.absoluteValue.toFloat() / total.toFloat()) * 360f * animateRotation

                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = centerTitle,
                    fontSize = 12.sp,
                    color = TextGreyLight,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = centerValue,
                    fontSize = 24.sp, // Larger text
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center, // Center the legend
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 3
        ) {
            data.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(item.color, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = item.name, fontSize = 12.sp, color = TextGreyLight)
                }
            }
        }
    }
}