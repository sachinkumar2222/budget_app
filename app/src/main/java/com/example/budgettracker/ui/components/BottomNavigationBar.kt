package com.example.budgettracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgettracker.R
import com.example.budgettracker.ui.theme.GlassyBorder
import com.example.budgettracker.ui.theme.TextGreyLight

// 1. Define items.
sealed class BottomNavItem(val name: String, val route: String, val iconRes: Int) {
    object Dashboard : BottomNavItem("Dashboard", "dashboard", R.drawable.ic_dashboard)
    object Income : BottomNavItem("Income", "income", R.drawable.ic_income)
    object Expense : BottomNavItem("Expense", "expense", R.drawable.ic_expense)
    object Profile : BottomNavItem("Profile", "profile", R.drawable.ic_profile)
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Income,
        BottomNavItem.Expense,
        BottomNavItem.Profile
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp) // Floating margin
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50)) // Pill shape
                .background(Color.Black.copy(alpha = 0.8f)) // Darker transparent background
                .border(1.dp, GlassyBorder, RoundedCornerShape(50))
                .padding(8.dp), // Padding inside the pill container
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "BgAnimation"
                )

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else TextGreyLight,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "ContentAnimation"
                )

                // Scale Animation for Icon
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.2f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "ScaleAnimation"
                )

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onItemClick(item.route) }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.name,
                            tint = contentColor,
                            modifier = Modifier
                                .size(20.dp)
                                .scale(scale) // Apply scale animation
                        )

                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Row {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    color = contentColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}