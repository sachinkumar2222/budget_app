package com.example.budgettracker.data.data

data class DashboardResponse(
    val totalBalance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val recentTransactions: List<TransactionDto>,
    val last30DaysExpense: ExpenseChartData?,
    val last60DaysIncome: IncomeChartData?
)

data class TransactionDto(
    val _id: String,
    val userId: String,
    val type: String, // "income" or "expense"
    val icon: String?, // URL from API (e.g. https://cdn.jsdelivr...)
    val category: String?, // For expenses
    val source: String?,   // For income
    val amount: Double,
    val date: String
)

data class ExpenseChartData(
    val total: Double,
    val transactions: List<TransactionDto>
)

data class IncomeChartData(
    val total: Double,
    val transactions: List<TransactionDto>
)

data class ExpenseResponse(
    val expense: List<TransactionDto>
)

data class AddTransactionRequest(
    val category: String, // For expense
    val amount: String,   // Server accepts string or number, usually string for inputs
    val date: String,     // "YYYY-MM-DD"
    val icon: String
)

data class AddTransactionResponse(
    val message: String?,
    val _id: String,
    val amount: Double,
    val category: String?,
    val date: String
)

data class AddIncomeRequest(
    val source: String,   // API expects "source"
    val amount: String,
    val date: String,
    val icon: String
)

data class AddIncomeResponse(
    val message: String?,
    val _id: String,
    val source: String?,
    val amount: Double,
    val date: String
)

data class DeleteResponse(
    val message: String,
    val success: Boolean
)