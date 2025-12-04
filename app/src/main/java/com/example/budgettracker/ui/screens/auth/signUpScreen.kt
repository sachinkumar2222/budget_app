package com.example.budgettracker.ui.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.budgettracker.R
import com.example.budgettracker.ui.theme.AppPurple
import com.example.budgettracker.ui.theme.BlackBackground
import com.example.budgettracker.ui.theme.NeonPurple
import com.example.budgettracker.ui.theme.TextGreyLight
import com.example.budgettracker.ui.theme.TextWhite
import com.example.budgettracker.viewmodel.AuthUiState
import com.example.budgettracker.ui.components.FancyToast
import com.example.budgettracker.ui.components.ToastType
import com.example.budgettracker.ui.components.BackgroundGradient
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    uiState: AuthUiState,
    onSignUpClick: (String, String, String, Uri?) -> Unit,
    onResetState: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordHidden by remember { mutableStateOf(true) }
    var termsAccepted by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.SignUpSuccess -> {
                toastMessage = "Account Created Successfully!"
                toastType = ToastType.SUCCESS
                showToast = true
                delay(1500)
                onSignUpSuccess()
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
            // Top Bar with Back Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Titles
            Text(
                text = "Let's Get Started",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "Fill the form to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGreyLight,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileImageUploader(
                imageUri = imageUri,
                onImageClick = { launcher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name
            Text(
                text = "Full Name",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextWhite,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = textFieldColors,
                placeholder = { Text("John Doe", color = Color.Gray) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            Text(
                text = "Your Email Address",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextWhite,
                modifier = Modifier.align(Alignment.Start)
            )
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

            // Password Field
            Text(
                text = "Choose a Password",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextWhite,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = textFieldColors,
                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                placeholder = { Text("min, 8 characters", color = Color.Gray) },
                trailingIcon = {
                    val image = if (passwordHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = TextGreyLight)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // "I agree" Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "I agree with terms of use",
                    fontSize = 14.sp,
                    color = TextWhite
                )
                Switch(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonPurple,
                        checkedTrackColor = NeonPurple.copy(alpha = 0.5f),
                        uncheckedThumbColor = TextGreyLight,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Sign Up Button with Gradient
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
                    .clickable(enabled = termsAccepted && uiState != AuthUiState.Loading) {
                        onSignUpClick(fullName, email, password, imageUri)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState == AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        "Sign Up",
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
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(style = SpanStyle(color = NeonPurple, fontWeight = FontWeight.Bold)) {
                        append("Login")
                    }
                },
                modifier = Modifier.clickable { onNavigateToLogin() },
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

@Composable
private fun ProfileImageUploader(
    imageUri: Uri?,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2A40)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                val painter = rememberAsyncImagePainter(model = imageUri)
                Image(
                    painter = painter,
                    contentDescription = "Selected profile image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile placeholder",
                    modifier = Modifier.size(56.dp),
                    tint = TextGreyLight
                )
            }
        }

        // Upload button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-8).dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(NeonPurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Upload image",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen(
        onSignUpSuccess = {},
        onNavigateToLogin = {},
        onNavigateBack = {},
        uiState = AuthUiState.Idle,
        onSignUpClick = { _, _, _, _ -> },
        onResetState = {}
    )
}