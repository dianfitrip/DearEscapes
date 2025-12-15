package com.example.myapplication.ui.screen.detail


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dianfitri.dearescapes.ui.common.UiState
import com.dianfitri.dearescapes.ui.common.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    entertainmentId: Int,
    navController: NavController,
    viewModel: DetailViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    // Panggil data saat pertama kali dibuka
    LaunchedEffect(entertainmentId) {
        viewModel.getEntertainmentDetail(entertainmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Hiburan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    val data = state.data
                    DetailContent(data)
                }
                is UiState.Error -> {
                    Text(
                        text = state.errorMessage,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailContent(item: com.dianfitri.dearescapes.data.model.Entertainment) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Gambar Sampul Besar
        AsyncImage(
            model = item.photoUrl ?: "https://placehold.co/400x300.png",
            contentDescription = "Cover Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        // 2. Kontainer Informasi (melengkung ke atas sedikit agar estetik)
        Column(
            modifier = Modifier
                .offset(y = (-20).dp) // Geser ke atas menimpa gambar
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // Judul & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.rating.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Genre & Kategori Badge
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text(item.genre) })
                SuggestionChip(
                    onClick = {},
                    label = { Text(if (item.category == "watch") "Tontonan" else "Bacaan") },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Section
            Text("Status", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
            Text(
                text = when (item.status) {
                    "planned" -> "Rencana (Planned)"
                    "in_progress" -> "Sedang Berjalan"
                    "completed" -> "Selesai"
                    "dropped" -> "Dibatalkan"
                    else -> item.status
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Deskripsi Section
            Text("Deskripsi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}