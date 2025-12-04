package com.example.budgettracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.budgettracker.ui.theme.BlackBackground
import com.example.budgettracker.ui.theme.NeonCyan
import com.example.budgettracker.ui.theme.NeonPurple

@Composable
fun BackgroundGradient(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        // Blob 1: Top Left (Purple)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-100).dp)
                .size(400.dp)
                .blur(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // Blob 2: Bottom Right (Cyan)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(400.dp)
                .blur(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonCyan.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // Blob 3: Center (Subtle Mix)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
                .blur(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF202020), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
    }
}
