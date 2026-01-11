package com.example.myapplication.ui.view.statistic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Star
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
// [PENTING] Import ini wajib ada agar PastelColors & warna lainnya terbaca
import com.example.myapplication.ui.theme.* import com.example.myapplication.ui.viewmodel.PenyediaViewModel
import com.example.myapplication.ui.viewmodel.StatistikUiState
import com.example.myapplication.ui.viewmodel.StatistikViewModel

@Composable
fun HalamanStatistik(
    viewModel: StatistikViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // Panggil fungsi fetch saat halaman dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchStatistik()
    }

    val uiState = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground) // Menggunakan warna dari Color.kt
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // HEADER
        Text(
            text = "Insight Hiburanmu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CottonCandyBlue
        )
        Text(
            text = "Seberapa sering kamu healing?",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CONTENT
        when (uiState) {
            is StatistikUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CottonCandyBlue)
                }
            }
            is StatistikUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = Color.Red)
                }
            }
            is StatistikUiState.Success -> {
                val data = uiState.data
                if (data.totalEntry == 0) {
                    EmptyStatView()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp) // Supaya tidak ketutup navbar
                    ) {
                        // 1. KARTU RINGKASAN ATAS
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Total Hiburan",
                                    value = data.totalEntry.toString(),
                                    icon = Icons.Rounded.Analytics,
                                    color = CottonCandyBlue
                                )
                                SummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Rata-rata Rating",
                                    value = String.format("%.1f", data.averageRating),
                                    icon = Icons.Rounded.Star,
                                    color = PastelYellow // Menggunakan warna dari Color.kt
                                )
                            }
                        }

                        // 2. GRAFIK GENRE (Bar Chart Custom)
                        item {
                            ChartCard(title = "Genre Terfavorit") {
                                Column {
                                    data.genreDistribution.entries.forEachIndexed { index, entry ->
                                        // PastelColors sekarang diambil dari Color.kt
                                        val color = PastelColors[index % PastelColors.size]
                                        // Hitung persentase lebar bar (relatif terhadap total)
                                        val percentage = if (data.totalEntry > 0) entry.value.toFloat() / data.totalEntry.toFloat() else 0f

                                        GenreBarItem(
                                            label = entry.key,
                                            count = entry.value,
                                            percentage = percentage,
                                            color = color
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }

                        // 3. GRAFIK STATUS (Simple Progress List)
                        item {
                            ChartCard(title = "Status Menonton/Membaca") {
                                Column {
                                    // Warna status diambil dari Color.kt
                                    StatusItem("Planned", data.statusDistribution["planned"] ?: 0, ColorPlanned)
                                    StatusItem("In Progress", data.statusDistribution["in_progress"] ?: 0, ColorInProgress)
                                    StatusItem("Completed", data.statusDistribution["completed"] ?: 0, ColorCompleted)
                                    StatusItem("Dropped", data.statusDistribution["dropped"] ?: 0, ColorDropped)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-KOMPONEN UI ---

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CottonCandyBlue)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun GenreBarItem(label: String, count: Int, percentage: Float, color: Color) {
    // Animasi lebar bar
    val animatedWidth by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000),
        label = "BarWidthAnimation"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            Text(text = "$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Background Bar (Abu-abu tipis)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF0F0F0))
        ) {
            // Foreground Bar (Berwarna)
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth) // Mengisi lebar sesuai persentase
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

@Composable
fun StatusItem(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color.Gray)
        Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun EmptyStatView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Analytics,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Belum ada data statistik", color = Color.Gray)
        Text("Tambahkan hiburanmu dulu!", fontSize = 12.sp, color = Color.LightGray)
    }
}