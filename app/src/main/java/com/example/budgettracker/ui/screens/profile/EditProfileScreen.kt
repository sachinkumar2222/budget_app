package com.example.budgettracker.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.draw.shadow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.budgettracker.R
import com.example.budgettracker.ui.theme.*
import com.example.budgettracker.ui.components.FancyToast
import com.example.budgettracker.ui.components.ToastType
import com.example.budgettracker.viewmodel.ProfileUiState
import com.example.budgettracker.viewmodel.ProfileViewModel
import androidx.compose.animation.core.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Lock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val profileData by viewModel.profileData.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uploadingImage by viewModel.uploadingImage.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf(profileData.fullName) }
    var phoneNumber by remember { mutableStateOf(profileData.phoneNumber) }
    var address by remember { mutableStateOf(profileData.address) }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Toast State
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    LaunchedEffect(profileData) {
        fullName = profileData.fullName
        phoneNumber = profileData.phoneNumber
        address = profileData.address
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ProfileUiState.Success -> {
                toastMessage = state.message
                toastType = ToastType.SUCCESS
                showToast = true
                kotlinx.coroutines.delay(1500) // Show toast before navigating
                onBack()
                viewModel.resetState()
            }
            is ProfileUiState.Error -> {
                toastMessage = state.message
                toastType = ToastType.ERROR
                showToast = true
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImage(it) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BlackBackground, Color(0xFF1A1033), BlackBackground)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Image with Camera Icon
                Box(
                    modifier = Modifier.padding(vertical = 16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhiteTransparent)
                            .border(BorderStroke(3.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple))), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileData.profileImageUrl != null) {
                            AsyncImage(
                                model = profileData.profileImageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TextWhite.copy(alpha = 0.7f),
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        if (uploadingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = NeonCyan
                            )
                        }
                    }

                    // Camera Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer {
                                scaleX = scale
                                    scaleY = scale
                            }
                            .clip(CircleShape)
                            .background(NeonCyan)
                            .border(2.dp, BlackBackground, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Form Fields in GlassyCard
                com.example.budgettracker.ui.components.GlassyCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 16.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        // Full Name
                        EditProfileTextField(
                            label = "Full Name",
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = "Enter your full name",
                            icon = Icons.Default.Person,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                            )
                        )

                        // Email (Read-only)
                        EditProfileTextField(
                            label = "E-Mail",
                            value = profileData.email,
                            onValueChange = {},
                            enabled = false,
                            icon = Icons.Default.Lock
                        )

                        // Phone Number
                        EditProfileTextField(
                            label = "Mobile",
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = "Enter mobile number",
                            icon = Icons.Default.Phone,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Phone
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                            )
                        )

                        // Address
                        EditProfileTextField(
                            label = "Address",
                            value = address,
                            onValueChange = { address = it },
                            placeholder = "Enter address",
                            icon = Icons.Default.Home,
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.updateProfile(fullName, phoneNumber, address)
                                }
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Save Button
                Button(
                    onClick = {
                        viewModel.updateProfile(fullName, phoneNumber, address)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonPurple),
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
                                    colors = listOf(NeonCyan, NeonPurple)
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
                                text = "SAVE CHANGES",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Toast Notification
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp)
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
}

@Composable
fun EditProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    enabled: Boolean = true,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextGreyLight,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
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
                    tint = if (enabled) NeonCyan else TextGreyLight.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan.copy(alpha = 0.7f),
                unfocusedBorderColor = GlassyBorder,
                disabledBorderColor = GlassyBorder.copy(alpha = 0.3f),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                disabledTextColor = TextGreyLight,
                cursorColor = NeonCyan,
                focusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                unfocusedContainerColor = SurfaceWhiteTransparent.copy(alpha = 0.05f),
                disabledContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 2
        )
    }
}
