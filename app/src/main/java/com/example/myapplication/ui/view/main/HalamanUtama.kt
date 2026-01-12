package com.example.myapplication.ui.view.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.HomeUiState
import com.example.myapplication.ui.viewmodel.HomeViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel
import com.example.myapplication.ui.view.profile.HalamanProfil
import com.example.myapplication.ui.view.search.HalamanSearch
import com.example.myapplication.ui.view.statistic.HalamanStatistik
// IMPORT WIDGET YANG BARU KITA BUAT
import com.example.myapplication.ui.view.widget.CottonCandyBottomBar
import com.example.myapplication.ui.view.widget.HomeHeader
import com.example.myapplication.ui.view.widget.PosterCard


@Composable
fun HalamanHome(
    onDetailClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onLogout: () -> Unit,
    onEditProfileClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val username by viewModel.currentUsername.collectAsState()
    var activeMenu by rememberSaveable { mutableStateOf("Home") }
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getEntries()
    }

    Scaffold(
        topBar = {
            if (activeMenu == "Home") {
                // Sekarang kode header lebih ringkas
                HomeHeader(username = username)
            }
        },
        bottomBar = {
            // Kode bottom bar juga tinggal panggil
            CottonCandyBottomBar(
                currentMenu = activeMenu,
                onMenuSelected = { selected -> activeMenu = selected }
            )
        },
        floatingActionButton = {
            if (activeMenu == "Home") {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = CottonCandyBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                }
            }
        },
        containerColor = Color(0xFFF5F9FF)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (activeMenu) {
                "Home" -> ContentHome(uiState, viewModel, onDetailClick)
                "Search" -> HalamanSearch(
                    onDetailClick = onDetailClick
                )
                "Statistik" -> HalamanStatistik()
                "Profil" -> HalamanProfil(
                    onLogout = onLogout,
                    onEditClick = onEditProfileClick
                )
            }
        }
    }
}

// Content Home tetap di sini karena mengandung logika UI State (Loading/Success/Error)
// Tapi item card-nya sudah dipisah
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
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.data) { item ->
                        // PosterCard dipanggil dari file widget
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
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, color = Color.Gray)
        }
    }
}