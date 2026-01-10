
package com.example.myapplication.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.ui.viewmodel.HomeUiState
import com.example.myapplication.ui.viewmodel.HomeViewModel

// Definisi Warna Cotton Candy
val CottonCandyBlue = Color(0xFF89CFF0)
val SoftPink = Color(0xFFFFB7B2) // Opsional untuk aksen

@Composable
fun HalamanHome(
    onDetailClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    // State untuk menu aktif
    var activeMenu by remember { mutableStateOf("Home") }
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            Column {
                // Judul Aplikasi atau Logo kecil (Opsional)
                // Bar Menu Custom
                CottonCandyMenuBar(
                    activeMenu = activeMenu,
                    onMenuClick = { menu -> activeMenu = menu }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = CottonCandyBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Data")
            }
        },
        containerColor = Color(0xFFF5F9FF) // Background putih kebiruan sangat muda
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {
            // Logika Navigasi Sederhana berdasarkan Menu
            if (activeMenu == "Home") {
                ContentHome(uiState, viewModel, onDetailClick)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Fitur $activeMenu belum tersedia", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ContentHome(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onDetailClick: (Int) -> Unit
) {
    when (uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CottonCandyBlue)
            }
        }
        is HomeUiState.Success -> {
            if (uiState.data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada data hiburan.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 Kolom
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.data) { item ->
                        PosterCard(item = item, onClick = { onDetailClick(item.id) })
                    }
                }
            }
        }
        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                    Button(
                        onClick = { viewModel.getEntries() },
                        colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue)
                    ) {
                        Text("Coba Lagi")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Refresh, contentDescription = null)
                    }
                }
            }
        }
    }
}

// --- KOMPONEN MENU BAR ---
@Composable
fun CottonCandyMenuBar(
    activeMenu: String,
    onMenuClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 16.dp, bottom = 8.dp), // Padding atas agak lega
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Home", "Search", "Statistik").forEach { menu ->
            val isActive = activeMenu == menu
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onMenuClick(menu) }
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = menu,
                    fontSize = 16.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) CottonCandyBlue else Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Garis bawah indikator aktif
                Box(
                    modifier = Modifier
                        .width(if (isActive) 40.dp else 0.dp)
                        .height(3.dp)
                        .background(
                            color = if (isActive) CottonCandyBlue else Color.Transparent,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

// --- KOMPONEN KARTU POSTER (Mirip Gambar Referensi) ---
@Composable
fun PosterCard(item: EntriHiburan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Tinggi fix agar seragam
            .clickable { onClick() }
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Gambar Background Full
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (item.photo?.startsWith("http") == true) item.photo
                        else "http://10.0.2.2:3000/${item.photo}" // Sesuaikan URL
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.BrokenImage),
                placeholder = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.Image)
            )

            // 2. Gradient Shadow di Bawah agar teks terbaca
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            // 3. Badge Status (Pojok Kiri Atas)
            Surface(
                color = CottonCandyBlue,
                shape = RoundedCornerShape(bottomEnd = 12.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = item.status,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // 4. Info Judul & Rating (Pojok Kiri Bawah)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.rating}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Kategori (Read/Watch)
                    Text(
                        text = "|  ${item.category}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}