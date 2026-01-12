package com.example.myapplication.ui.view.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList // Icon Filter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.view.widget.PosterCard
import com.example.myapplication.ui.viewmodel.PenyediaViewModel
import com.example.myapplication.ui.viewmodel.SearchUiState
import com.example.myapplication.ui.viewmodel.SearchViewModel

// Daftar Genre Master
val masterGenreList = listOf(
    "Action", "Adventure", "Biography", "Classic", "Comedy", "Coming of Age",
    "Contemporary", "Crime", "Cultivation", "Cyberpunk", "Dark Fantasy", "Demons",
    "Detective", "Drama", "Dystopian", "Ecchi", "Fairy Tale", "Family", "Fantasy",
    "Fiction", "Game", "Gothic", "Harem", "Healing / Iyashikei", "Historical",
    "History", "Horror", "Isekai", "Josei", "Literary Fiction", "Low Fantasy",
    "Magic", "Martial Arts", "Mecha", "Military", "Music", "Mystery", "Mythology",
    "New Adult", "Noir", "Non-Fiction", "Parody", "Philosophy", "Police",
    "Post-Apocalyptic", "Psychological", "Psychological Horror", "Psychological Thriller",
    "Regression", "Romance", "Romantic Comedy", "Samurai", "School", "Science Fiction",
    "Sci-Fi", "Seinen", "Self-Improvement", "Short Story", "Shoujo", "Shoujo Ai",
    "Shounen", "Slice of Life", "Slow Burn", "Space", "Sports", "Steampunk",
    "Super Power", "Supernatural", "Survival", "Suspense", "System / Leveling",
    "Thriller", "Time Travel", "Urban Fantasy", "Vampire", "Villainess", "Wuxia",
    "Xianxia", "Young Adult (YA)"
).sorted()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanSearch(
    viewModel: SearchViewModel = viewModel(factory = PenyediaViewModel.Factory),
    onDetailClick: (Int) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState
    val softBlueBg = Color(0xFFF5F9FF)

    // State untuk Bottom Sheet
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = softBlueBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(top = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            // --- HEADER ---
            Text("Cari Hiburanmu", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CottonCandyBlue)
            Text("Ketik judul atau gunakan filter", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // --- SEARCH BAR UTAMA + FILTER BUTTON ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 1. Search Bar (Judul)
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.updateQuery(it) },
                    placeholder = { Text("Cari judul...", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = CottonCandyBlue) },
                    trailingIcon = {
                        if (viewModel.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateQuery("") }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, CircleShape, spotColor = CottonCandyBlue.copy(alpha = 0.2f))
                        .background(Color.White, CircleShape),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = CottonCandyBlue,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = CottonCandyBlue
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.performSearch()
                        focusManager.clearFocus()
                    })
                )

                Spacer(modifier = Modifier.width(12.dp))

                // 2. TOMBOL FILTER (Membuka Bottom Sheet)
                val isFilterActive = viewModel.selectedGenre != null
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(4.dp, CircleShape, spotColor = CottonCandyBlue.copy(alpha = 0.2f))
                        .clip(CircleShape)
                        .background(if (isFilterActive) CottonCandyBlue else Color.White)
                        .clickable { showFilterSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = if (isFilterActive) Color.White else CottonCandyBlue
                    )
                }
            }

            // --- INDIKATOR FILTER AKTIF ---
            if (viewModel.selectedGenre != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Genre aktif: ", fontSize = 12.sp, color = Color.Gray)
                    Surface(
                        color = CottonCandyBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { viewModel.selectGenre(null) } // Klik untuk hapus
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(viewModel.selectedGenre!!, color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Close, null, tint = CottonCandyBlue, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- HASIL PENCARIAN ---
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is SearchUiState.Idle -> EmptyStateContent(Icons.Default.Search, "Mulai Menjelajah", "Ketik sesuatu di atas")
                    is SearchUiState.Loading -> CircularProgressIndicator(color = CottonCandyBlue, modifier = Modifier.align(Alignment.Center))
                    is SearchUiState.Empty -> EmptyStateContent(Icons.Rounded.SearchOff, "Tidak Ditemukan", "Coba kata kunci atau genre lain")
                    is SearchUiState.Error -> Text(uiState.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    is SearchUiState.Success -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(uiState.data) { item ->
                                PosterCard(item = item, onClick = { onDetailClick(item.id) })
                            }
                        }
                    }
                }
            }
        }

        // --- BOTTOM SHEET GENRE ---
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                // Isi Bottom Sheet
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 40.dp)
                        .heightIn(max = 500.dp)
                ) {
                    // Header Sheet
                    Text("Pilih Genre", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CottonCandyBlue)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Search Genre (Lokal di dalam Sheet)
                    OutlinedTextField(
                        value = viewModel.genreSearchQuery,
                        onValueChange = { viewModel.updateGenreSearchQuery(it) },
                        placeholder = { Text("Cari genre...", color = Color.Gray, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CottonCandyBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color(0xFFF8FBFF),
                            unfocusedContainerColor = Color(0xFFF8FBFF)
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. List Genre (Filtered by local search)
                    val filteredGenres = masterGenreList.filter {
                        it.contains(viewModel.genreSearchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredGenres) { genre ->
                            val isSelected = viewModel.selectedGenre == genre
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CottonCandyBlue.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable {
                                        viewModel.selectGenre(genre)
                                        showFilterSheet = false // Tutup sheet setelah pilih
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) CottonCandyBlue else Color.LightGray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = genre,
                                    color = if (isSelected) CottonCandyBlue else Color.Black,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }
    }
}

// Komponen State Kosong
@Composable
fun BoxScope.EmptyStateContent(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String, subMessage: String) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = CottonCandyBlue.copy(alpha = 0.4f), modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, fontWeight = FontWeight.Bold, color = CottonCandyBlue, fontSize = 18.sp)
        Text(subMessage, fontSize = 12.sp, color = Color.Gray)
    }
}