package com.example.budgettracker.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.budgettracker.ui.components.BottomNavigationBar
import com.example.budgettracker.ui.components.InfoCard
import com.example.budgettracker.ui.components.TransactionItem
import com.example.budgettracker.ui.components.charts.BarChartData
import com.example.budgettracker.ui.components.charts.CustomBarChart
import com.example.budgettracker.ui.components.charts.CustomPieChart
import com.example.budgettracker.ui.components.charts.FinancialHealthChart
import com.example.budgettracker.ui.components.charts.PieChartData
import com.example.budgettracker.ui.theme.*
import com.example.budgettracker.viewmodel.DashboardUiState
import com.example.budgettracker.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.budgettracker.ui.components.BackgroundGradient
import com.example.budgettracker.ui.components.GlassyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchDashboardData()
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
            is DashboardUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            }
            is DashboardUiState.Error -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = NeonPink,
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.fetchDashboardData() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan
                        )
                    ) {
                        Text("Retry", color = Color.Black)
                    }
                }
            }
            is DashboardUiState.Success -> {
                DashboardContent(
                    data = state.data,
                    userName = userState?.fullName ?: "Alexa",
                    userImage = userState?.profileImageUrl,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    onNavigate = onNavigate
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
                currentRoute = "dashboard",
                onItemClick = onNavigate
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    data: com.example.budgettracker.data.data.DashboardResponse,
    userName: String,
    userImage: String?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 50.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Header
            item {
                DashboardHeader(userName, userImage)
            }

            // 2. Info Cards
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    InfoCard(
                        icon = Icons.Default.CreditCard,
                        label = "Total Balance",
                        value = "₹${data.totalBalance.toInt()}",
                        iconBgColor = NeonCyan,
                        iconTint = Color.Black
                    )
                    InfoCard(
                        icon = Icons.Default.AccountBalanceWallet,
                        label = "Total Income",
                        value = "₹${data.totalIncome.toInt()}",
                        iconBgColor = NeonMint,
                        iconTint = Color.Black
                    )
                    InfoCard(
                        icon = Icons.Default.MoneyOff,
                        label = "Total Expense",
                        value = "₹${data.totalExpense.toInt()}",
                        iconBgColor = NeonOrange,
                        iconTint = Color.Black
                    )
                }
            }

            item {
                if (data.totalIncome > 0 || data.totalExpense > 0) {
                    val score = if (data.totalIncome > 0) {
                        ((data.totalBalance / data.totalIncome) * 100).coerceIn(0.0, 100.0).toFloat()
                    } else {
                        0f
                    }

                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        FinancialHealthChart(
                            score = score,
                            totalIncome = data.totalIncome,
                            totalExpense = data.totalExpense,
                            totalBalance = data.totalBalance,
                            title = "Financial Health"
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val recentList = data.recentTransactions.take(5)

                    GlassyCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (recentList.isEmpty()) {
                            Text("No recent transactions", color = Color.Gray, modifier = Modifier.padding(8.dp))
                        } else {
                            Column {
                                recentList.forEachIndexed { index, transaction ->
                                    val title = if (transaction.type == "expense") transaction.category else transaction.source
                                    TransactionItem(
                                        title = title ?: "Unknown",
                                        iconUrl = transaction.icon,
                                        dateString = transaction.date,
                                        amount = transaction.amount,
                                        type = transaction.type
                                    )
                                    if (index < recentList.lastIndex) {
                                        HorizontalDivider(
                                            color = Color.White.copy(alpha = 0.1f),
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                val transactions = data.last30DaysExpense?.transactions ?: emptyList()
                val barData = transactions
                    .groupBy { it.date.take(10) }
                    .map { (dateStr, list) ->
                        val total = list.sumOf { it.amount }
                        val parsedDate = try {
                            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
                            SimpleDateFormat("dd", Locale.getDefault()).format(input!!)
                        } catch (e: Exception) { dateStr }
                        BarChartData(label = parsedDate, value = total, color = NeonPurple)
                    }
                    .sortedBy { it.label }
                    .takeLast(7)
                    .mapIndexed { index, barChartData ->
                        barChartData.copy(color = if (index % 2 == 0) NeonCyan else NeonBlue)
                    }

                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CustomBarChart(
                        title = "Last 30 Days Expenses",
                        data = barData
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CardTitleWithSeeAll("Expense") {
                        onNavigate("expense")
                    }

                    val expenseList = data.last30DaysExpense?.transactions?.take(4)

                    GlassyCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (expenseList.isNullOrEmpty()) {
                            Text("No expenses recorded", color = Color.Gray)
                        } else {
                            Column {
                                expenseList.forEachIndexed { index, expense ->
                                    TransactionItem(
                                        title = expense.category ?: "Expense",
                                        iconUrl = expense.icon,
                                        dateString = expense.date,
                                        amount = expense.amount,
                                        type = "expense"
                                    )
                                    if (index < expenseList.lastIndex) {
                                        HorizontalDivider(
                                            color = Color.White.copy(alpha = 0.1f),
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                val incomeTransactions = data.last60DaysIncome?.transactions
                if (!incomeTransactions.isNullOrEmpty()) {
                    val colors = listOf(
                        NeonCyan, NeonPurple, NeonGreen, NeonPink, NeonYellow, NeonOrange, NeonMint, NeonBlue
                    )
                    val incomePieData = incomeTransactions
                        .groupBy { it.source ?: "Other" }
                        .map { (source, list) -> source to list.sumOf { it.amount } }
                        .filter { it.second > 0 }
                        .mapIndexed { index, (source, amount) ->
                            PieChartData(
                                name = source,
                                value = amount,
                                color = colors[index % colors.size]
                            )
                        }

                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        CustomPieChart(
                            title = "Last 60 Days Income",
                            data = incomePieData,
                            centerTitle = "Total Income",
                            centerValue = "₹${data.totalIncome.toInt()}"
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CardTitleWithSeeAll("Income") {
                        onNavigate("income")
                    }

                    val incomeList = data.last60DaysIncome?.transactions?.take(4)

                    GlassyCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (incomeList.isNullOrEmpty()) {
                            Text("No income recorded", color = Color.Gray)
                        } else {
                            Column {
                                incomeList.forEachIndexed { index, income ->
                                    TransactionItem(
                                        title = income.source ?: "Income",
                                        iconUrl = income.icon,
                                        dateString = income.date,
                                        amount = income.amount,
                                        type = "income"
                                    )
                                    if (index < incomeList.lastIndex) {
                                        HorizontalDivider(
                                            color = Color.White.copy(alpha = 0.1f),
                                            thickness = 1.dp
                                        )
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

@Composable
fun DashboardHeader(userName: String, userImage: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        // Top Bar: "Dashboard" and Profile Pic
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.2f))
                    .border(BorderStroke(1.dp, NeonCyan), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (userImage != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Greeting Section
        Text(
            text = "Hello,",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = userName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "👋",
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun CardTitleWithSeeAll(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        
        // Enhanced "See All" Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = NeonCyan.copy(alpha = 0.15f)
                )
                .border(
                    width = 1.dp,
                    color = NeonCyan.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onSeeAllClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "See All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeonCyan
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "See All",
                    tint = NeonCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
