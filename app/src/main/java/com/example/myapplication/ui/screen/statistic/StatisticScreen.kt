package com.example.myapplication.ui.screen.statistic


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.common.UiState
import com.example.myapplication.ui.common.ViewModelFactory

@Composable
fun StatisticScreen(
    navController: NavController,
    viewModel: StatisticViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getStatistics()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Statistik Saya",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                val stat = state.data

                // Menampilkan Grid Kartu Statistik
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatisticCard(
                        title = "Total Entri",
                        value = stat.totalEntries.toString(),
                        icon = Icons.Default.Book,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        title = "Selesai",
                        value = stat.completedCount.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatisticCard(
                        title = "Rating Rata2",
                        value = stat.averageRating.toString(),
                        icon = Icons.Default.Star,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        title = "Genre Favorit",
                        value = stat.favoriteGenre,
                        icon = Icons.Default.Favorite,
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Tambahan: Progress Bar Sederhana
                Spacer(modifier = Modifier.height(32.dp))
                Text("Progres Penyelesaian", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                val progress = if (stat.totalEntries > 0) stat.completedCount.toFloat() / stat.totalEntries.toFloat() else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                )
                Text(
                    text = "${(progress * 100).toInt()}% Selesai",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Column {
                Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(text = title, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}