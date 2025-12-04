package com.example.budgettracker.ui.screens.expense

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgettracker.data.data.TransactionDto
import com.example.budgettracker.ui.components.BottomNavigationBar
import com.example.budgettracker.ui.components.GradientButton
import com.example.budgettracker.ui.components.TransactionItem
import com.example.budgettracker.ui.components.charts.CustomLineChart
import com.example.budgettracker.ui.components.charts.LineChartData
import com.example.budgettracker.ui.screens.income.formatDateToDayMonth
import com.example.budgettracker.ui.theme.BlackBackground
import com.example.budgettracker.ui.theme.GlassyBackground
import com.example.budgettracker.ui.theme.GlassyBorder
import com.example.budgettracker.ui.theme.NeonPink
import com.example.budgettracker.ui.theme.TextGreyLight
import com.example.budgettracker.ui.theme.TextWhite
import com.example.budgettracker.viewmodel.ExpenseUiState
import com.example.budgettracker.viewmodel.ExpenseViewModel
import com.example.budgettracker.ui.components.FancyToast
import androidx.compose.ui.zIndex
import com.example.budgettracker.ui.components.DeleteConfirmationDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.budgettracker.ui.components.GlassyCard
import com.example.budgettracker.ui.components.BackgroundGradient

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    onAddExpenseClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState by viewModel.toastState.collectAsStateWithLifecycle()
    val currentRoute = "expense"

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTransactionId by remember { mutableStateOf<String?>(null) }
    
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchExpenses()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BackgroundGradient()

        when (val state = uiState) {
            is ExpenseUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonPink)
                }
            }
            is ExpenseUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is ExpenseUiState.Success -> {
                ExpenseContent(
                    transactions = state.transactions,
                    onAddExpenseClick = onAddExpenseClick,
                    onDownloadClick = { viewModel.downloadReport() },
                    onDeleteClick = { id ->
                        selectedTransactionId = id
                        showDeleteDialog = true
                    },
                    contentPadding = PaddingValues(0.dp)
                )
            }
        }

        // Floating Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemClick = onNavigate
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
                .zIndex(10f)
        ) {
            FancyToast(
                message = toastState.message,
                type = toastState.type,
                isVisible = toastState.show,
                onDismiss = { viewModel.hideToast() }
            )
        }

        if (showDeleteDialog && selectedTransactionId != null) {
            DeleteConfirmationDialog(
                title = "Delete Expense?",
                message = "Are you sure you want to delete this expense record? This action cannot be undone.",
                onConfirm = {
                    selectedTransactionId?.let { viewModel.deleteExpense(it) }
                    showDeleteDialog = false
                    selectedTransactionId = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    selectedTransactionId = null
                }
            )
        }
    }
}

@Composable
fun ExpenseContent(
    transactions: List<TransactionDto>,
    onAddExpenseClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    contentPadding: PaddingValues
) {
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) { listState.scrollToItem(0) }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 50.dp,
            bottom = contentPadding.calculateBottomPadding() + 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Expense",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }
        }

        // 1. Expense Overview Section (Line Chart)
        item {
            GlassyCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Expense Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("Track your spending trends", fontSize = 12.sp, color = TextGreyLight)
                        }
                        GradientButton(
                            text = "Add Expense",
                            icon = Icons.Default.Add,
                            onClick = onAddExpenseClick
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val lineData = remember(transactions) {
                        try {
                            transactions
                                .sortedBy { it.date }
                                .takeLast(30)
                                .map {
                                    LineChartData(
                                        label = formatDateToDayMonth(it.date),
                                        value = it.amount
                                    )
                                }
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }

                    if (lineData.isNotEmpty()) {
                        CustomLineChart(
                            title = "",
                            data = lineData,
                            modifier = Modifier.height(250.dp),
                            lineColor = NeonPink,
                            fillColor = NeonPink.copy(alpha = 0.1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No expense data available", color = TextGreyLight)
                        }
                    }
                }
            }
        }

        // 2. Expense List Section
        item {
            GlassyCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Expense List", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                        OutlinedButton(
                            onClick = onDownloadClick,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            border = BorderStroke(1.dp, TextGreyLight),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = TextGreyLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download", color = TextGreyLight, fontSize = 12.sp)
                        }
                    }

                    if (transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("No expenses found", color = TextGreyLight)
                        }
                    } else {
                        Column {
                            transactions.forEachIndexed { index, transaction ->
                                TransactionItem(
                                    title = transaction.category ?: "Expense",
                                    iconUrl = transaction.icon,
                                    dateString = transaction.date,
                                    amount = transaction.amount,
                                    type = "expense",
                                    onLongClick = { onDeleteClick(transaction._id) }
                                )

                                if (index < transactions.lastIndex) {
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}