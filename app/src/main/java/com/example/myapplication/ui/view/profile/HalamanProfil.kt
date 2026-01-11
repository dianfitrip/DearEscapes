package com.example.myapplication.ui.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.* import com.example.myapplication.ui.viewmodel.PenyediaViewModel
import com.example.myapplication.ui.viewmodel.ProfileViewModel
import kotlin.math.abs

@Composable
fun HalamanProfil(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = PenyediaViewModel.Factory),
    onLogout: () -> Unit,
    onEditClick: () -> Unit
) {
    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()
    val stats by viewModel.uiStats.collectAsState()

    // [MODIFIKASI] State untuk Dialog Logout
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchProfileData()
    }

    // --- DIALOG VALIDASI LOGOUT ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah anda yakin ingin keluar dari aplikasi?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftPink) // Warna Merah Muda
                ) {
                    Text("Ya, Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoftBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- 1. HEADER: TOMBOL LOGOUT ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // [MODIFIKASI] Tombol ini sekarang memicu Dialog, bukan langsung logout
            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = SoftPink
                )
            }
        }

        // --- 2. AVATAR & INFO USER ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = getCuteAvatarProfil(username),
                contentDescription = null,
                tint = CottonCandyBlue,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Halo, $username!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CottonCandyBlue
        )
        Text(
            text = "Your Quiet Escapes",
            fontSize = 14.sp,
            color = Color.Gray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
        Text(
            text = email,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- 3. STATISTIK GRID ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Planned",
                count = stats.planned.toString(),
                color = ColorPlanned,
                icon = Icons.Rounded.Event
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "In Progress",
                count = stats.inProgress.toString(),
                color = ColorInProgress,
                icon = Icons.Rounded.Schedule
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Completed",
                count = stats.completed.toString(),
                color = ColorCompleted,
                icon = Icons.Rounded.CheckCircle
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Dropped",
                count = stats.dropped.toString(),
                color = ColorDropped,
                icon = Icons.Rounded.Cancel
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- 4. TOMBOL EDIT PROFIL ---
        Button(
            onClick = onEditClick,
            colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Edit Profil", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ... (Kode StatCard dan getCuteAvatarProfil di bawah tetap sama) ...
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun getCuteAvatarProfil(username: String): ImageVector {
    val icons = listOf(
        Icons.Rounded.Face, Icons.Rounded.Pets, Icons.Rounded.Cloud,
        Icons.Rounded.Star, Icons.Rounded.Favorite, Icons.Rounded.Spa,
        Icons.Rounded.AcUnit, Icons.Rounded.AutoAwesome, Icons.Rounded.EmojiNature,
        Icons.Rounded.WbSunny
    )
    if (username.isEmpty()) return Icons.Rounded.Face
    val index = abs(username.hashCode()) % icons.size
    return icons[index]
}