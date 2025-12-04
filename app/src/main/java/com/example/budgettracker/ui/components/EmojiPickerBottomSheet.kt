package com.example.budgettracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.budgettracker.util.EmojiCategory
import com.example.budgettracker.util.EmojiHelper
import com.example.budgettracker.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPickerBottomSheet(
    onDismiss: () -> Unit,
    onIconSelected: (String) -> Unit,
    accentColor: Color = NeonPurple // Default accent color
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Async Data Loading
    val allCategoriesState = produceState<List<EmojiCategory>>(initialValue = emptyList()) {
        value = withContext(Dispatchers.IO) {
            EmojiHelper.getCategorizedEmojis()
        }
    }
    val allCategories = allCategoriesState.value
    val isLoading = allCategories.isEmpty()

    var searchQuery by remember { mutableStateOf("") }

    // FILTER LOGIC
    val filteredCategories = remember(searchQuery, allCategories) {
        if (searchQuery.isBlank()) {
            allCategories
        } else {
            allCategories.filter { category ->
                category.title.contains(searchQuery, ignoreCase = true) ||
                        category.keywords.any { keyword -> keyword.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        modifier = Modifier.padding(top = 40.dp),
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            // Blurred background layer with Transparent Black
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent black
                    .blur(radius = 30.dp) // Increased blur for better glass effect
            )
            
            // Content layer (not blurred)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = GlassyBorder.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .fillMaxHeight(0.85f) // Limit height
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp, bottom = 20.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(accentColor.copy(alpha = 0.5f), RoundedCornerShape(50))
                    )
                    
                    Text(
                        text = "Pick an Icon",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "Select an icon for your transaction",
                        fontSize = 14.sp,
                        color = TextGreyLight,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search icons...", color = TextGreyLight.copy(alpha = 0.7f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Search, 
                                contentDescription = null, 
                                tint = accentColor 
                            ) 
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = GlassyBorder.copy(alpha = 0.3f),
                            focusedBorderColor = accentColor,
                            focusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                            unfocusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = accentColor
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = accentColor)
                        }
                    } else {
                        // Categorized Grid with 5 columns
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 50.dp)
                        ) {
                            filteredCategories.forEach { category ->
                                // Category Header
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp, 16.dp)
                                                .background(accentColor, RoundedCornerShape(2.dp))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = category.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextWhite
                                        )
                                    }
                                }

                                // Emojis
                                items(category.icons) { url ->
                                    // Entrance Animation
                                    var isVisible by remember { mutableStateOf(false) }
                                    LaunchedEffect(Unit) { isVisible = true }

                                    AnimatedVisibility(
                                        visible = isVisible,
                                        enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeIn()
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(SurfaceWhiteTransparent.copy(alpha = 0.05f))
                                                .border(
                                                    width = 1.dp,
                                                    color = GlassyBorder.copy(alpha = 0.3f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .clickable {
                                                    onIconSelected(url)
                                                    onDismiss()
                                                }
                                        ) {
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Emoji",
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Show message if no results found
                            if (filteredCategories.isEmpty()) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(40.dp), 
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No icons found", color = TextGreyLight)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}