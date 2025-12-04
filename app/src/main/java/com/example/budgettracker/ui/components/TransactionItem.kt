package com.example.budgettracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.budgettracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    title: String,
    iconUrl: String?,
    dateString: String,
    amount: Double,
    type: String, // "income" or "expense"
    onLongClick: () -> Unit = {}
) {
    val isIncome = type == "income"

    // Colors based on type
    val amountColor = if (isIncome) NeonMint else NeonPink
    // Use translucent backgrounds for the pills in dark mode
    val amountBgColor = amountColor.copy(alpha = 0.1f)
    val trendIcon = if (isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown

    // Format Date
    val formattedDate = formatTransactionDate(dateString)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- ICON SECTION (Loads from URL) ---
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(SurfaceWhiteLessTransparent) // Translucent Grey
                .border(BorderStroke(1.dp, GlassyBorder), CircleShape), // Glassy Border
            contentAlignment = Alignment.Center
        ) {
            if (!iconUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback Icon
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = TextGreyLight,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // --- TITLE & DATE ---
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextGreyLight
            )
        }

        // --- AMOUNT PILL ---
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(amountBgColor)
                .border(BorderStroke(1.dp, amountColor.copy(alpha = 0.2f)), RoundedCornerShape(8.dp)) // Subtle border
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${if(isIncome) "+" else "-"} ₹${amount.toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Icon(
                imageVector = trendIcon,
                contentDescription = null,
                tint = amountColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Formatting Helper
fun formatTransactionDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(isoDate)
        // Format: 12 Nov 2023
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        outputFormat.format(date ?: return isoDate.take(10))
    } catch (e: Exception) {
        // Fallback to simple YYYY-MM-DD if parsing fails
        isoDate.take(10)
    }
}