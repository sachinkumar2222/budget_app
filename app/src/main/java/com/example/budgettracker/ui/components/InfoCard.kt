package com.example.budgettracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgettracker.ui.theme.TextGreyLight
import com.example.budgettracker.ui.theme.TextWhite

@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconBgColor: Color,
    iconTint: Color = Color.White
) {
    GlassyCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box with Gradient
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                iconBgColor.copy(alpha = 0.7f),
                                iconBgColor
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Label and Value
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = TextGreyLight,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }
    }
}