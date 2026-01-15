package com.example.myapplication.ui.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.PenyediaViewModel
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
    val scrollState = rememberScrollState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Refresh data saat halaman dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBFF))
            .verticalScroll(scrollState)
    ) {

//header

        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CottonCandyBlue,
                                CottonCandyBlue.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Profil Saya",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            4.dp,
                            Color.White
                        ),
                        modifier = Modifier.size(110.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.background(Color(0xFFE3F2FD))
                        ) {
                            Icon(
                                imageVector = getCuteAvatar(username),
                                contentDescription = null,
                                tint = CottonCandyBlue,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }

                    SmallFloatingActionButton(
                        onClick = onEditClick,
                        containerColor = Color.White,
                        contentColor = CottonCandyBlue,
                        modifier = Modifier
                            .size(36.dp)
                            .offset(x = 4.dp, y = 4.dp),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(email, fontSize = 14.sp, color = Color.Gray)
            }
        }

//rangkuman data

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "Ringkasan Aktivitas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CottonCandyBlue,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            StatCardWide(
                "Koleksi Disimpan",
                stats.total.toString(),
                Icons.Rounded.Bookmarks,
                CottonCandyBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {
                StatCardSmall(
                    Modifier.weight(1f),
                    "Selesai",
                    stats.completed.toString(),
                    Icons.Rounded.CheckCircle,
                    Color(0xFF4CAF50),
                    Color(0xFFE8F5E9)
                )
                Spacer(Modifier.width(16.dp))
                StatCardSmall(
                    Modifier.weight(1f),
                    "Sedang Baca",
                    stats.inProgress.toString(),
                    Icons.Rounded.AutoStories,
                    Color(0xFF2196F3),
                    Color(0xFFE3F2FD)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {
                StatCardSmall(
                    Modifier.weight(1f),
                    "Rencana",
                    stats.planned.toString(),
                    Icons.Rounded.DateRange,
                    Color(0xFFFF9800),
                    Color(0xFFFFF3E0)
                )
                Spacer(Modifier.width(16.dp))
                StatCardSmall(
                    Modifier.weight(1f),
                    "Drop",
                    stats.dropped.toString(),
                    Icons.Rounded.DeleteSweep,
                    Color(0xFFF44336),
                    Color(0xFFFFEBEE)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

//logout button

        Button(
            onClick = { showLogoutDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFEBEE),
                contentColor = Color(0xFFD32F2F)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp)
                .shadow(0.dp)
        ) {
            Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Keluar Akun", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(40.dp))
    }

    //konfirmasi dialog logout

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Logout",
                    fontWeight = FontWeight.Bold,
                    color = CottonCandyBlue
                )
            },
            text = {
                Text("Apakah kamu yakin ingin keluar dari akun ini?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CottonCandyBlue
                    )
                ) {
                    Text("Ya, Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

//random profil

@Composable
fun getCuteAvatar(username: String): ImageVector {
    val icons = listOf(
        Icons.Rounded.Face,
        Icons.Rounded.Pets,
        Icons.Rounded.Cloud,
        Icons.Rounded.Star,
        Icons.Rounded.Favorite,
        Icons.Rounded.Spa,
        Icons.Rounded.AcUnit,
        Icons.Rounded.AutoAwesome,
        Icons.Rounded.EmojiNature,
        Icons.Rounded.WbSunny
    )
    val index = if (username.isEmpty()) 0 else abs(username.hashCode()) % icons.size
    return icons[index]
}

@Composable
fun StatCardWide(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(0.1f))
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(title, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatCardSmall(
    modifier: Modifier,
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(
            Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bgColor)
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
