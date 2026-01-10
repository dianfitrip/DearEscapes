package com.example.myapplication.ui.view.entry

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.ui.view.home.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.DetailEntri
import com.example.myapplication.ui.viewmodel.EntryUiState
import com.example.myapplication.ui.viewmodel.EntryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntry(
    navigateBack: () -> Unit,
    viewModel: EntryViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState() // State untuk scroll

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tambah Hiburan",
                        color = CottonCandyBlue,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                .fillMaxSize() // Agar bisa discroll penuh
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Container Form (Card)
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
                            coroutineScope.launch {
                                viewModel.saveEntry(navigateBack)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EntryBody(
    entryUiState: EntryUiState,
    onValueChange: (DetailEntri) -> Unit,
    onSaveClick: () -> Unit
) {
    val detail = entryUiState.detailEntri

    // Launcher untuk membuka Galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onValueChange(detail.copy(photo = uri.toString()))
        }
    }

    // 1. Judul (Icon Cloud)
    CuteTextField(
        value = detail.title,
        onValueChange = { onValueChange(detail.copy(title = it)) },
        label = "Judul Hiburan",
        icon = Icons.Default.Cloud
    )

    // 2. Kategori
    Text("Kategori", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Opsi 1: Watch (Dibungkus Box weight 1f agar bagi rata)
        Box(modifier = Modifier.weight(1f)) {
            CategoryChip(
                text = "Watch",
                icon = Icons.Default.Movie,
                selected = detail.category == "watch",
                onSelected = { onValueChange(detail.copy(category = "watch")) }
            )
        }

        // Opsi 2: Read (Dibungkus Box weight 1f agar bagi rata)
        Box(modifier = Modifier.weight(1f)) {
            CategoryChip(
                text = "Read",
                icon = Icons.Default.Book,
                selected = detail.category == "read",
                onSelected = { onValueChange(detail.copy(category = "read")) }
            )
        }
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
    CuteTextField(
        value = detail.genre,
        onValueChange = { onValueChange(detail.copy(genre = it)) },
        label = "Genre (misal: Drama, Scifi)",
        icon = Icons.Default.Check
    )

    // 5. INPUT FOTO
    Text("Foto Cover", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    ImagePickerBox(
        imageUri = if (detail.photo.isNotEmpty()) Uri.parse(detail.photo) else null,
        onClick = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    )

    // 6. Rating
    Text("Rating Anda", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    RatingInput(
        rating = detail.rating.toDoubleOrNull() ?: 0.0,
        onRatingChanged = { onValueChange(detail.copy(rating = it.toString())) }
    )

    // 7. Status (Update: 4 Pilihan sesuai Database)
    Text("Status", color = CottonCandyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)

    // Baris 1: Planned & In Progress
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            CategoryChip(
                text = "Planned",
                selected = detail.status == "planned",
                onSelected = { onValueChange(detail.copy(status = "planned")) }
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            CategoryChip(
                text = "In Progress",
                selected = detail.status == "in_progress",
                onSelected = { onValueChange(detail.copy(status = "in_progress")) }
            )
        }
    }

    // Baris 2: Completed & Dropped
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            CategoryChip(
                text = "Completed",
                selected = detail.status == "completed",
                onSelected = { onValueChange(detail.copy(status = "completed")) }
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            CategoryChip(
                text = "Dropped",
                selected = detail.status == "dropped",
                onSelected = { onValueChange(detail.copy(status = "dropped")) }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Tombol Simpan
    Button(
        onClick = onSaveClick,
        enabled = entryUiState.isEntryValid,
        colors = ButtonDefaults.buttonColors(
            containerColor = CottonCandyBlue,
            disabledContainerColor = Color.LightGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(4.dp, CircleShape),
        shape = CircleShape
    ) {
        Text("Simpan Catatan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

// --- KOMPONEN CUSTOM ---

@Composable
fun ImagePickerBox(
    imageUri: Uri?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F8FF))
            .border(
                border = BorderStroke(2.dp, CottonCandyBlue.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                shape = CircleShape
            ) {
                Text(
                    "Ganti",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = CottonCandyBlue,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Klik untuk unggah foto", color = CottonCandyBlue, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        leadingIcon = if (icon != null) {
            { Icon(icon, contentDescription = null, tint = CottonCandyBlue) }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CottonCandyBlue,
            unfocusedBorderColor = CottonCandyBlue.copy(alpha = 0.4f),
            cursorColor = CottonCandyBlue,
            focusedContainerColor = Color(0xFFF0F8FF),
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun CategoryChip(
    text: String,
    icon: ImageVector? = null,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        color = if (selected) CottonCandyBlue else Color.White,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, if (selected) CottonCandyBlue else Color.LightGray),
        modifier = Modifier
            .fillMaxWidth() // Agar mengisi Box weight
            .clip(RoundedCornerShape(50))
            .clickable { onSelected() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // Teks di tengah
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (selected) Color.White else Color.Gray,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp // Sedikit diperkecil agar muat
            )
        }
    }
}

@Composable
fun RatingInput(rating: Double, onRatingChanged: (Double) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = "Star $i",
                tint = Color(0xFFFFD700),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(i.toDouble()) }
                    .padding(2.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "($rating)",
            color = CottonCandyBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}