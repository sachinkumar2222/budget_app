package com.example.budgettracker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgettracker.ui.screens.auth.LoginScreen
import com.example.budgettracker.ui.screens.auth.SignUpScreen
import com.example.budgettracker.ui.screens.dashboard.DashboardScreen
import com.example.budgettracker.ui.screens.expense.AddExpenseScreen
import com.example.budgettracker.ui.screens.expense.ExpenseScreen
import com.example.budgettracker.ui.screens.income.AddIncomeScreen
import com.example.budgettracker.ui.screens.income.IncomeScreen
import com.example.budgettracker.ui.screens.profile.EditProfileScreen
import com.example.budgettracker.ui.screens.profile.ProfileScreen
import com.example.budgettracker.ui.screens.splash.SplashScreen
import com.example.budgettracker.ui.theme.NeonCyan
import com.example.budgettracker.viewmodel.AuthUiState
import com.example.budgettracker.viewmodel.AuthViewModel
import com.example.budgettracker.viewmodel.DashboardUiState
import com.example.budgettracker.viewmodel.DashboardViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val DASHBOARD = "dashboard"
    const val INCOME = "income"
    const val EXPENSE = "expense"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ADD_EXPENSE = "add_expense"
    const val ADD_INCOME = "add_income"
}

@Composable
fun AppNavGraph(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Global Listener for Token Expiration (Logout)
    LaunchedEffect(Unit) {
        viewModel.logoutSignal.collect {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // ▼▼▼ HELPER FUNCTION FOR BOTTOM NAV ▼▼▼
    val onBottomNavClick: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.DASHBOARD) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // 1. SPLASH SCREEN
        composable(Routes.SPLASH) {
            LaunchedEffect(Unit) { viewModel.checkAuthStatus() }

            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Authenticated) {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            }

            SplashScreen(
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // 2. LOGIN SCREEN
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    viewModel.resetState()
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    viewModel.resetState()
                    navController.navigate(Routes.SIGNUP)
                },
                onNavigateBack = {
                    viewModel.resetState()
                    navController.navigate(Routes.SPLASH) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                uiState = uiState,
                onLoginClick = { email, password, rememberMe ->
                    viewModel.login(email, password, rememberMe)
                },
                onResetState = { viewModel.resetState() }
            )
        }

        // 3. SIGN UP SCREEN
        composable(Routes.SIGNUP) {
            SignUpScreen(
                onSignUpSuccess = {
                    viewModel.resetState()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    viewModel.resetState()
                    navController.popBackStack()
                },
                onNavigateBack = {
                    viewModel.resetState()
                    navController.popBackStack()
                },
                uiState = uiState,
                onSignUpClick = { fullName, email, password, uri ->
                    viewModel.signUp(fullName, email, password, uri)
                },
                onResetState = { viewModel.resetState() }
            )
        }

        // 4. DASHBOARD SCREEN
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigate = onBottomNavClick
            )
        }

        // 5. INCOME SCREEN
        composable(Routes.INCOME) {
            IncomeScreen(
                onNavigate = onBottomNavClick,
                onAddIncomeClick = {
                    navController.navigate(Routes.ADD_INCOME)
                }
            )
        }

        composable(Routes.ADD_INCOME) {
            AddIncomeScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 6. EXPENSE SCREEN
        composable(Routes.EXPENSE) {
            ExpenseScreen(
                onNavigate = { route ->
                    if (route != Routes.EXPENSE) {
                        onBottomNavClick(route)
                    }
                },
                onAddExpenseClick = {
                    navController.navigate(Routes.ADD_EXPENSE)
                }
            )
        }

        composable(Routes.ADD_EXPENSE) {
            AddExpenseScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 7. PROFILE SCREEN
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigate = onBottomNavClick,
                onEditProfileClick = {
                    navController.navigate(Routes.EDIT_PROFILE)
                },
                onLogoutClick = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 8. EDIT PROFILE SCREEN
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}