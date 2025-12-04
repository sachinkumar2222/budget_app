package com.example.budgettracker.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgettracker.ui.theme.*
import com.example.budgettracker.viewmodel.ProfileUiState
import com.example.budgettracker.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState) {
        when (uiState) {
            is ProfileUiState.Success -> {
                onDismiss()
                viewModel.resetState()
            }
            is ProfileUiState.Error -> {
                errorMessage = (uiState as ProfileUiState.Error).message
            }
            else -> {}
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            // Blurred background layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .blur(radius = 30.dp)
            )
            
            // Content layer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = GlassyBorder.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(NeonCyan.copy(alpha = 0.5f))
                    )

                    // Header Icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhiteTransparent.copy(alpha = 0.05f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Change Password",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Old Password
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { 
                                oldPassword = it
                                errorMessage = ""
                            },
                            label = { Text("Old Password", color = TextGreyLight) },
                            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Key,
                                    contentDescription = null,
                                    tint = NeonCyan
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                    Icon(
                                        imageVector = if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TextGreyLight
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = GlassyBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                cursorColor = NeonCyan,
                                focusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                                unfocusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )

                        // New Password
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { 
                                newPassword = it
                                errorMessage = ""
                            },
                            label = { Text("New Password", color = TextGreyLight) },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = NeonCyan
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TextGreyLight
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = GlassyBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                cursorColor = NeonCyan,
                                focusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                                unfocusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )
                    }

                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = NeonPink,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Save Button
                    Button(
                        onClick = {
                            when {
                                oldPassword.isBlank() -> errorMessage = "Please enter old password"
                                newPassword.isBlank() -> errorMessage = "Please enter new password"
                                newPassword.length < 6 -> errorMessage = "Password must be at least 6 characters"
                                else -> {
                                    viewModel.changePassword(oldPassword, newPassword)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonCyan),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp),
                        enabled = uiState !is ProfileUiState.Loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(NeonCyan, NeonBlue)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState is ProfileUiState.Loading) {
                                CircularProgressIndicator(
                                    color = TextWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "UPDATE PASSWORD",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
