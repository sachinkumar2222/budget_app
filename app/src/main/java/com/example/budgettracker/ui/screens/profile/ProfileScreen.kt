package com.example.budgettracker.ui.screens.profile

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.budgettracker.R
import com.example.budgettracker.ui.components.BottomNavigationBar
import com.example.budgettracker.ui.theme.*
import com.example.budgettracker.viewmodel.ProfileViewModel
import com.example.budgettracker.viewmodel.AuthViewModel
import com.example.budgettracker.ui.components.FancyToast
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.blur
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.budgettracker.ui.components.BackgroundGradient
import com.example.budgettracker.ui.components.GlassyCard

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val profileData by viewModel.profileData.collectAsStateWithLifecycle()
    val notificationEnabled by viewModel.notificationEnabled.collectAsStateWithLifecycle()
    val toastState by authViewModel.toastState.collectAsStateWithLifecycle()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Lifecycle Observer for Refresh
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "profile",
                onItemClick = onNavigate
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BackgroundGradient()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
                    .blur(radius = if (showPasswordDialog) 10.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                }

                // User Info Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhiteTransparent),
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
                                tint = TextWhite,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome",
                            fontSize = 12.sp,
                            color = TextGreyLight
                        )
                        Text(
                            text = profileData.fullName.ifBlank { "User" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Menu Items
                GlassyCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = "User Profile",
                            onClick = onEditProfileClick
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        ProfileMenuItem(
                            icon = Icons.Default.Lock,
                            title = "Change Password",
                            onClick = { showPasswordDialog = true }
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        ProfileMenuItem(
                            icon = Icons.Default.Info,
                            title = "FAQs",
                            onClick = { /* TODO */ }
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        // Push Notification Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = TextGreyLight,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Push Notification",
                                    fontSize = 16.sp,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Switch(
                                checked = notificationEnabled,
                                onCheckedChange = { viewModel.toggleNotification(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonGreen,
                                    checkedTrackColor = NeonGreen.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TextGreyLight,
                                    uncheckedTrackColor = TextGreyLight.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                // Help Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(NeonCyan.copy(alpha = 0.1f))
                        .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "If you have any other query you\ncan reach out to us.",
                            fontSize = 14.sp,
                            color = TextWhite,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "WhatsApp Us",
                            fontSize = 14.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { /* TODO */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Change Password Dialog
            if (showPasswordDialog) {
                ChangePasswordDialog(
                    viewModel = viewModel,
                    onDismiss = { showPasswordDialog = false }
                )
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) { }
                        .zIndex(5f)
                    ) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text("Logout", color = TextWhite, fontWeight = FontWeight.Bold) },
                        text = { Text("Are you sure you want to logout?", color = TextGreyLight) },
                        confirmButton = {
                            TextButton(onClick = {
                                showLogoutDialog = false
                                onLogoutClick()
                            }) {
                                Text("Yes", color = NeonPink, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text("Cancel", color = TextGreyLight)
                            }
                        },
                        containerColor = Color.Black.copy(alpha = 0.9f),
                        tonalElevation = 0.dp,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 40.dp)
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )
                }
            }
            
            // Toast notification
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 50.dp)
                    .zIndex(10f)
            ) {
                FancyToast(
                    message = toastState.message,
                    type = toastState.type,
                    isVisible = toastState.show,
                    onDismiss = { authViewModel.hideToast() }
                )
            }

            // Profile ViewModel Toast
            val profileToastState by viewModel.toastState.collectAsStateWithLifecycle()
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .zIndex(10f)
            ) {
                FancyToast(
                    message = profileToastState.message,
                    type = profileToastState.type,
                    isVisible = profileToastState.show,
                    onDismiss = { viewModel.hideToast() }
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextGreyLight,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = TextWhite,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextGreyLight,
            modifier = Modifier.size(20.dp)
        )
    }
}