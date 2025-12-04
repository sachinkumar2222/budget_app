
package com.example.budgettracker.ui.screens.income

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.budgettracker.R
import com.example.budgettracker.ui.components.EmojiPickerBottomSheet
import com.example.budgettracker.ui.components.FancyToast
import com.example.budgettracker.ui.components.GlassyCard
import com.example.budgettracker.ui.components.GradientButton
import com.example.budgettracker.ui.theme.*
import com.example.budgettracker.viewmodel.IncomeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    viewModel: IncomeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var source by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedIconUrl by remember { mutableStateOf<String?>(null) }
    var showIconPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val addState by viewModel.addIncomeState.collectAsState()
    val toastState by viewModel.toastState.collectAsStateWithLifecycle()

    LaunchedEffect(addState) {
        if (addState == true) {
            delay(3000) // Wait for toast to be visible
            viewModel.resetAddIncomeState()
            onBack()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        date = formatter.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("OK", color = NeonMint) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = NeonMint)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFF1E1E1E),
                titleContentColor = TextWhite,
                headlineContentColor = TextWhite,
                weekdayContentColor = TextGreyLight,
                subheadContentColor = TextGreyLight,
                yearContentColor = TextGreyLight,
                currentYearContentColor = NeonMint,
                selectedYearContentColor = TextWhite,
                selectedYearContainerColor = NeonMint,
                dayContentColor = TextWhite,
                disabledDayContentColor = Color.Gray,
                selectedDayContentColor = TextWhite,
                selectedDayContainerColor = NeonMint,
                todayContentColor = NeonMint,
                todayDateBorderColor = NeonMint
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF1E1E1E),
                    titleContentColor = TextWhite,
                    headlineContentColor = TextWhite,
                    weekdayContentColor = TextGreyLight,
                    subheadContentColor = TextGreyLight,
                    yearContentColor = TextGreyLight,
                    currentYearContentColor = NeonMint,
                    selectedYearContentColor = TextWhite,
                    selectedYearContainerColor = NeonMint,
                    dayContentColor = TextWhite,
                    disabledDayContentColor = Color.Gray,
                    selectedDayContentColor = TextWhite,
                    selectedDayContainerColor = NeonMint,
                    todayContentColor = NeonMint,
                    todayDateBorderColor = NeonMint
                )
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Add Income", 
                        fontWeight = FontWeight.Bold, 
                        color = TextWhite,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BlackBackground, Color(0xFF001F15), BlackBackground) // Dark Greenish tint for Income
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .blur(radius = if (showIconPicker) 10.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                
                GlassyCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 24.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        
                        // Icon Picker
                        Text("Icon", fontSize = 14.sp, color = TextGreyLight, fontWeight = FontWeight.Medium)
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceWhiteTransparent.copy(alpha = 0.05f))
                                .border(1.dp, GlassyBorder, RoundedCornerShape(16.dp))
                                .clickable { showIconPicker = true }
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedIconUrl != null) Color.Transparent else SurfaceWhiteTransparent.copy(alpha = 0.1f))
                                    .border(1.dp, if (selectedIconUrl != null) NeonMint else Color.Transparent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedIconUrl != null) {
                                    AsyncImage(
                                        model = selectedIconUrl,
                                        contentDescription = "Selected Icon",
                                        modifier = Modifier.size(36.dp)
                                    )
                                } else {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = NeonMint)
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (selectedIconUrl != null) "Icon Selected" else "Select Icon",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Tap to change",
                                    fontSize = 12.sp,
                                    color = TextGreyLight
                                )
                            }
                        }

                        // Source Input
                        IncomeInputTextField(
                            label = "Income Source",
                            value = source,
                            onValueChange = { source = it },
                            placeholder = "Salary, Freelance etc",
                            icon = Icons.Default.Category
                        )

                        // Amount Input
                        IncomeInputTextField(
                            label = "Amount",
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = "0.00",
                            icon = Icons.Default.AttachMoney,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        // Date Input
                        IncomeInputTextField(
                            label = "Date",
                            value = date,
                            onValueChange = {},
                            enabled = false,
                            placeholder = "Select Date",
                            icon = Icons.Default.CalendarToday,
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Submit Button
                GradientButton(
                    text = "Add Income",
                    icon = Icons.Default.Add,
                    onClick = {
                        if (source.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty() && selectedIconUrl != null) {
                            viewModel.addIncome(source, amount, date, selectedIconUrl!!)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonMint),
                    gradient = Brush.horizontalGradient(listOf(NeonMint, NeonCyan))
                )
            }

            // Toast notification
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp)
                    .zIndex(10f)
            ) {
                FancyToast(
                    message = toastState.message,
                    type = toastState.type,
                    isVisible = toastState.show,
                    onDismiss = { viewModel.hideToast() }
                )
            }
        }
    }

    if (showIconPicker) {
        EmojiPickerBottomSheet(
            onDismiss = { showIconPicker = false },
            onIconSelected = { url ->
                selectedIconUrl = url
                showIconPicker = false
            },
            accentColor = NeonMint
        )
    }
}

@Composable
fun IncomeInputTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextGreyLight,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = { Text(placeholder, color = TextGreyLight.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) NeonMint else TextGreyLight.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            },
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonMint.copy(alpha = 0.7f),
                unfocusedBorderColor = GlassyBorder,
                disabledBorderColor = GlassyBorder.copy(alpha = 0.3f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                disabledTextColor = TextWhite, // Keep text white even if disabled (for Date)
                cursorColor = NeonMint,
                focusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                unfocusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                disabledContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
