package com.example.myapplication.ui.screen.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.common.UiState
import com.example.myapplication.ui.common.ViewModelFactory
import com.example.myapplication.ui.screen.home.component.EntertainmentItem

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    // Ambil data saat halaman pertama kali dibuka
    LaunchedEffect(Unit) {
        viewModel.getAllEntertainments()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add New")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    val data = state.data
                    if (data.isEmpty()) {
                        Text(
                            text = "Belum ada data. Tambahkan sekarang!",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(data) { item ->
                                EntertainmentItem(
                                    title = item.title,
                                    genre = item.genre,
                                    photoUrl = item.photoUrl,
                                    rating = item.rating,
                                    onClick = {
                                        // Navigasi ke detail dengan membawa ID (nanti kita buat fitur detail)
                                        navController.navigate("detail/${item.id}")
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            // Di HomeScreen, tambahkan tombol ini (misal di Row TopBar)
            IconButton(onClick = { navController.navigate("statistic") }) {
                Icon(imageVector = Icons.Default.PieChart, contentDescription = "Statistik")
            }
        }
    }
}