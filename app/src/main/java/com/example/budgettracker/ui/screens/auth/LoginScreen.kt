package com.example.budgettracker.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgettracker.R
import com.example.budgettracker.ui.theme.AppPurple
import com.example.budgettracker.ui.theme.BlackBackground
import com.example.budgettracker.ui.theme.NeonPurple
import com.example.budgettracker.ui.theme.TextGreyLight
import com.example.budgettracker.ui.theme.TextWhite
import androidx.compose.ui.graphics.asComposeRenderEffect
import android.graphics.RenderEffect as AndroidRenderEffect
import android.os.Build
import com.example.budgettracker.viewmodel.AuthUiState
import androidx.compose.ui.zIndex
import com.example.budgettracker.ui.components.FancyToast
import com.example.budgettracker.ui.components.ToastType
import com.example.budgettracker.ui.components.BackgroundGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateBack: () -> Unit,
    uiState: AuthUiState,
    onLoginClick: (String, String, Boolean) -> Unit,
    onResetState: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.LoginSuccess -> {
                toastMessage = "Login Successful!"
                toastType = ToastType.SUCCESS
                showToast = true
                kotlinx.coroutines.delay(1000)
                onLoginSuccess()
            }
            is AuthUiState.Error -> {
                toastMessage = uiState.message
                toastType = ToastType.ERROR
                showToast = true
            }
            else -> {}
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = NeonPurple,
        focusedBorderColor = NeonPurple,
        unfocusedBorderColor = TextGreyLight,
        focusedLabelColor = NeonPurple,
        unfocusedLabelColor = TextGreyLight
    )

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGradient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp),
                        tint = TextWhite
                    )
                }
                Text(
                    text = "Forgot password?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGreyLight,
                    modifier = Modifier.clickable { /* Forgot password logic */ }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Logo Area
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                renderEffect = AndroidRenderEffect
                                    .createBlurEffect(30f, 30f, android.graphics.Shader.TileMode.DECAL)
                                    .asComposeRenderEffect()
                            }
                        }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonPurple.copy(alpha = 0.3f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(240.dp)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Inputs
            Text("Email Address", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextWhite, modifier = Modifier.align(Alignment.Start).fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = textFieldColors,
                placeholder = { Text("john.doe@gmail.com", color = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Password", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextWhite, modifier = Modifier.align(Alignment.Start).fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = textFieldColors,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = TextGreyLight)
                    }
                },
                placeholder = { Text("••••••••", color = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Remember Me
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Remember me next time", fontSize = 14.sp, color = TextWhite)
                Switch(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonPurple,
                        checkedTrackColor = NeonPurple.copy(alpha = 0.5f),
                        uncheckedThumbColor = TextGreyLight,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Login Button with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF9C27B0),
                                Color(0xFF7B1FA2)
                            )
                        )
                    )
                    .clickable(enabled = uiState != AuthUiState.Loading) {
                        onLoginClick(email, password, rememberMe)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState == AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        "Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Footer
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 50.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Don't have an account? ")
                    withStyle(style = SpanStyle(color = NeonPurple, fontWeight = FontWeight.Bold)) {
                        append("Sign up")
                    }
                },
                modifier = Modifier.clickable { onNavigateToSignUp() },
                color = TextGreyLight
            )
        }

        // Toast
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 65.dp)
                .zIndex(10f)
        ) {
            FancyToast(
                message = toastMessage,
                type = toastType,
                isVisible = showToast,
                onDismiss = { showToast = false }
            )
        }
    }
}