package com.example.myapplication.ui.view.entry

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.DetailEntri
import com.example.myapplication.ui.viewmodel.EntryUiState
import com.example.myapplication.ui.viewmodel.EntryViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel

val genreList = listOf(
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
fun HalamanEntry(
    navigateBack: () -> Unit,
    viewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showConfirmDialog by remember { mutableStateOf(false) }

    //kerangka halaman “Tambah Hiburan” yang mengatur AppBar, layout form, dan validasi sebelum data disimpan
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Hiburan", color = CottonCandyBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CottonCandyBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F9FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EntryBody(
                        entryUiState = viewModel.uiState,
                        onValueChange = viewModel::updateUiState,
                        onSaveClick = {
                            if (viewModel.uiState.isEntryValid) {
                                showConfirmDialog = true
                            } else {
                                Toast.makeText(context, "Mohon lengkapi Judul dan Deskripsi!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    //DIALOG KONFIRMASI SIMPAN - KHUSUS HALAMAN ENTRY
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Simpan", fontWeight = FontWeight.Bold, color = CottonCandyBlue) },
            text = { Text("Apakah kamu ingin menyimpan data hiburan ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.saveEntry(context, navigateBack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue)
                ) { Text("Ya, Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Batal", color = Color.Gray) }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EntryBody(
    entryUiState: EntryUiState,
    onValueChange: (DetailEntri) -> Unit,
    onSaveClick: () -> Unit
) {
    val detail = entryUiState.detailEntri
    var showGenreDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onValueChange(detail.copy(photo = uri.toString()))
        }
    }

    // 1. Judul
    CuteTextField(
        value = detail.title,
        onValueChange = { onValueChange(detail.copy(title = it)) },
        label = "Judul Hiburan",
        icon = Icons.Default.Cloud
    )

    // 2. Kategori
    Text("Kategori", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) { CategoryChip("Watch", Icons.Default.Movie, detail.category == "watch") { onValueChange(detail.copy(category = "watch")) } }
        Box(modifier = Modifier.weight(1f)) { CategoryChip("Read", Icons.Default.Book, detail.category == "read") { onValueChange(detail.copy(category = "read")) } }
    }

    // 3. Deskripsi
    CuteTextField(
        value = detail.description,
        onValueChange = { onValueChange(detail.copy(description = it)) },
        label = "Deskripsi Singkat",
        icon = Icons.Default.Description,
        singleLine = false
    )

    // 4. Genre
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = detail.genre,
            onValueChange = {}, readOnly = true,
            label = { Text("Genre (Pilih)", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = CottonCandyBlue) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = CottonCandyBlue) },
            modifier = Modifier.fillMaxWidth(), enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = CottonCandyBlue.copy(alpha = 0.4f),
                disabledLabelColor = Color.Gray,
                disabledLeadingIconColor = CottonCandyBlue,
                disabledTrailingIconColor = CottonCandyBlue,
                disabledContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )
        Box(modifier = Modifier.matchParentSize().clickable { showGenreDialog = true })
    }

    if (showGenreDialog) {
        GenreSelectionDialog(
            initialSelection = detail.genre,
            onDismiss = { showGenreDialog = false },
            onSave = { selectedGenres ->
                onValueChange(detail.copy(genre = selectedGenres))
                showGenreDialog = false
            }
        )
    }

    // 5. Foto
    Text("Foto Cover", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    ImagePickerBox(
        imageUri = if (detail.photo.isNotEmpty()) Uri.parse(detail.photo) else null,
        onClick = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    )

    // 6. Rating
    Text("Rating Anda", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    RatingInput(
        rating = detail.rating.toDoubleOrNull() ?: 0.0,
        onRatingChanged = { onValueChange(detail.copy(rating = it.toString())) }
    )

    // 7. Status
    Text("Status", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) { CategoryChip("Planned", null, detail.status == "planned") { onValueChange(detail.copy(status = "planned")) } }
        Box(modifier = Modifier.weight(1f)) { CategoryChip("In Progress", null, detail.status == "in_progress") { onValueChange(detail.copy(status = "in_progress")) } }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) { CategoryChip("Completed", null, detail.status == "completed") { onValueChange(detail.copy(status = "completed")) } }
        Box(modifier = Modifier.weight(1f)) { CategoryChip("Dropped", null, detail.status == "dropped") { onValueChange(detail.copy(status = "dropped")) } }
    }

    Spacer(modifier = Modifier.height(16.dp))

    //TOMBOL SIMPAN
    Button(
        onClick = onSaveClick,
        enabled = true,
        colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue, disabledContainerColor = Color.LightGray),
        modifier = Modifier.fillMaxWidth().height(50.dp).shadow(4.dp, CircleShape),
        shape = CircleShape
    ) {
        Text("Simpan Catatan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

//KOMPONEN PENDUKUNG (GenreSelectionDialog, ImagePickerBox, dll)
@Composable
fun GenreSelectionDialog(initialSelection: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    val initialList = initialSelection.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    val selectedItems = remember { mutableStateListOf<String>().apply { addAll(initialList) } }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pilih Genre", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CottonCandyBlue, modifier = Modifier.padding(bottom = 12.dp))
                Divider(color = Color.LightGray)
                LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                    items(genreList) { genre ->
                        val isSelected = selectedItems.contains(genre)
                        Row(modifier = Modifier.fillMaxWidth().clickable { if (isSelected) selectedItems.remove(genre) else selectedItems.add(genre) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isSelected, onCheckedChange = { if (isSelected) selectedItems.remove(genre) else selectedItems.add(genre) }, colors = CheckboxDefaults.colors(checkedColor = CottonCandyBlue))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = genre, fontSize = 16.sp)
                        }
                    }
                }
                Divider(color = Color.LightGray)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSave(selectedItems.sorted().joinToString(", ")) }, colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue)) { Text("Pilih") }
                }
            }
        }
    }
}

//ImagePickerBox berfungsi sebagai komponen antarmuka untuk memilih, menampilkan, dan mengganti foto cover hiburan
@Composable
fun ImagePickerBox(imageUri: Uri?, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF0F8FF)).border(BorderStroke(2.dp, CottonCandyBlue.copy(alpha = 0.5f)), RoundedCornerShape(16.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        if (imageUri != null) {
            AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Surface(color = Color.Black.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp), shape = CircleShape) { Text("Ganti", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddPhotoAlternate, null, tint = CottonCandyBlue, modifier = Modifier.size(48.dp))
                Text("Klik untuk unggah foto", color = CottonCandyBlue, fontSize = 12.sp)
            }
        }
    }
}

//Menyediakan input teks dengan tampilan konsisten dan ramah pengguna
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuteTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector? = null, singleLine: Boolean = true) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label, color = Color.Gray) }, leadingIcon = if (icon != null) { { Icon(icon, null, tint = CottonCandyBlue) } } else null, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = singleLine, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CottonCandyBlue, unfocusedBorderColor = CottonCandyBlue.copy(alpha = 0.4f), cursorColor = CottonCandyBlue, focusedContainerColor = Color(0xFFF0F8FF), unfocusedContainerColor = Color.Transparent))
}

//Menampilkan chip pilihan kategori atau status yang dapat dipilih oleh pengguna.
@Composable
fun CategoryChip(text: String, icon: ImageVector? = null, selected: Boolean, onSelected: () -> Unit) {
    Surface(color = if (selected) CottonCandyBlue else Color.White, shape = RoundedCornerShape(50), border = BorderStroke(1.dp, if (selected) CottonCandyBlue else Color.LightGray), modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).clickable { onSelected() }) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (icon != null) { Icon(icon, null, tint = if (selected) Color.White else Color.Gray, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(8.dp)) }
            Text(text = text, color = if (selected) Color.White else Color.Gray, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
        }
    }
}

//CategoryChip berfungsi sebagai komponen untuk menentukan kategori atau status
@Composable
fun RatingInput(rating: Double, onRatingChanged: (Double) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) { Icon(imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarBorder, contentDescription = "Star $i", tint = Color(0xFFFFD700), modifier = Modifier.size(40.dp).clickable { onRatingChanged(i.toDouble()) }.padding(2.dp)) }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "($rating)", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}