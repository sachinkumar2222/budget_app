package com.example.budgettracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// Enum to define the type of toast
enum class ToastType {
    SUCCESS, ERROR, WARNING, INFO
}

// Helper data class to hold style info
private data class ToastStyle(
    val backgroundColor: Color,
    val icon: ImageVector,
    val iconColor: Color,
    val borderColor: Color
)

@Composable
fun FancyToast(
    message: String,
    type: ToastType,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    // Animation state tracking
    var animationStage by remember { mutableStateOf(0) }
    
    // Reset animation when visibility changes
    LaunchedEffect(isVisible) {
        if (isVisible) {
            animationStage = 0
            delay(50) // Small delay before starting
            animationStage = 1 // Start bubble drop
            delay(300)
            animationStage = 2 // Start horizontal expansion
            delay(300)
            animationStage = 3 // Show message
            delay(2400) // Show for 2.4 seconds after full animation
            onDismiss()
        } else {
            animationStage = 0
        }
    }

    // Map ToastType to ToastStyle using neon theme colors
    val style = when (type) {
        ToastType.SUCCESS -> ToastStyle(
            backgroundColor = Color(0xFF1A2E1A), // Dark green background
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF4ADE80), // Neon green
            borderColor = Color(0xFF4ADE80).copy(alpha = 0.3f)
        )
        ToastType.ERROR -> ToastStyle(
            backgroundColor = Color(0xFF2E1A1A), // Dark red background
            icon = Icons.Default.Error,
            iconColor = Color(0xFFFF6B6B), // Neon red
            borderColor = Color(0xFFFF6B6B).copy(alpha = 0.3f)
        )
        ToastType.WARNING -> ToastStyle(
            backgroundColor = Color(0xFF2E2A1A), // Dark yellow background
            icon = Icons.Default.Warning,
            iconColor = Color(0xFFFBBF24), // Neon yellow
            borderColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
        )
        ToastType.INFO -> ToastStyle(
            backgroundColor = Color(0xFF1A1E2E), // Dark blue background
            icon = Icons.Default.Info,
            iconColor = Color(0xFF60A5FA), // Neon blue
            borderColor = Color(0xFF60A5FA).copy(alpha = 0.3f)
        )
    }

    // Animated values
    val offsetY by animateDpAsState(
        targetValue = if (animationStage >= 1) 0.dp else (-100).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetY"
    )

    val width by animateDpAsState(
        targetValue = if (animationStage >= 2) 280.dp else 48.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "width"
    )

    val height by animateDpAsState(
        targetValue = 48.dp,
        animationSpec = tween(300),
        label = "height"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (animationStage >= 2) 12.dp else 24.dp,
        animationSpec = tween(300),
        label = "cornerRadius"
    )

    val messageAlpha by animateFloatAsState(
        targetValue = if (animationStage >= 3) 1f else 0f,
        animationSpec = tween(200),
        label = "messageAlpha"
    )

    val closeAlpha by animateFloatAsState(
        targetValue = if (animationStage >= 3) 1f else 0f,
        animationSpec = tween(200),
        label = "closeAlpha"
    )

    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .zIndex(10f),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .width(width)
                    .height(height)
                    .background(style.backgroundColor, shape = RoundedCornerShape(cornerRadius))
                    .border(1.5.dp, style.borderColor, shape = RoundedCornerShape(cornerRadius))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Icon - always visible
                Icon(
                    imageVector = style.icon,
                    contentDescription = null,
                    tint = style.iconColor,
                    modifier = Modifier.size(24.dp)
                )
                
                // Message - fades in during stage 3
                if (animationStage >= 2) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .weight(1f)
                            .alpha(messageAlpha),
                        maxLines = 1
                    )
                }
                
                // Close button - fades in during stage 3
                if (animationStage >= 3) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(18.dp)
                            .alpha(closeAlpha)
                            .clickable { onDismiss() }
                    )
                }
            }
        }
    }
}