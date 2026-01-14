package com.example.myapplication.ui.view.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.model.EntriHiburan
// Import Warna
import com.example.myapplication.ui.theme.*
import com.example.myapplication.ui.viewmodel.DetailUiState
import com.example.myapplication.ui.viewmodel.DetailViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel

@Composable
fun HalamanDetail(
    id: Int,
    navigateBack: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.getDetailHiburan(id)
    }

    val uiState = viewModel.uiState
    val currentEntry = (uiState as? DetailUiState.Success)?.entri

    // --- DIALOG HAPUS YANG LUCU ---
    if (showDeleteDialog && currentEntry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Rounded.DeleteForever, null, tint = StrongPink) },
            title = {
                Text(
                    text = "Hapus Kenangan?",
                    color = CottonCandyBlue,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Yakin ingin menghapus \"${currentEntry.title}\"?\nData yang dihapus tidak bisa kembali lho.. \uD83D\uDE22",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEntri(
                            id = id,
                            onSuccess = {
                                Toast.makeText(context, "Sudah dihapus ya!", Toast.LENGTH_SHORT).show()
                                navigateBack()
                            },
                            onError = { message ->
                                Toast.makeText(context, "Ups error: $message", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StrongPink),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Hapus Aja")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Jangan Deh", color = CottonCandyBlue, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = WhiteCard,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEdit(id) },
                shape = RoundedCornerShape(16.dp), // Squircle shape
                containerColor = CottonCandyBlue,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit")
            }
        },
        containerColor = SoftBackground
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CottonCandyBlue)
                    }
                }
                is DetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.CloudOff, null, tint = Color.Gray, modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Gagal memuat data...", color = Color.Gray)
                            Button(
                                onClick = { viewModel.getDetailHiburan(id) },
                                colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue),
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
                is DetailUiState.Success -> {
                    DetailContent(
                        entri = uiState.entri,
                        navigateBack = navigateBack,
                        onDeleteClick = { showDeleteDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailContent(
    entri: EntriHiburan,
    navigateBack: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // --- HEADER GAMBAR BESAR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp) // Lebih tinggi sedikit
        ) {
            val imageUrl = when {
                entri.photo.isNullOrEmpty() -> ""
                entri.photo.startsWith("http") ||
                        entri.photo.startsWith("content://") ||
                        entri.photo.startsWith("file://") -> entri.photo
                else -> "http://10.0.2.2:3000/${entri.photo}"
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = entri.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = rememberVectorPainter(Icons.Default.BrokenImage)
            )

            // Gradient halus di bawah gambar agar transisi ke putih mulus
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.6f)),
                            startY = 500f
                        )
                    )
            )

            // TOMBOL NAVIGASI DI ATAS GAMBAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tombol Back (Blurry Circle)
                IconButton(
                    onClick = navigateBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(WhiteCard.copy(alpha = 0.7f))
                        .size(44.dp)
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = CottonCandyBlue)
                }

                // Tombol Delete (Blurry Circle)
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(WhiteCard.copy(alpha = 0.7f))
                        .size(44.dp)
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = StrongPink)
                }
            }
        }

        // --- KARTU KONTEN (Melengkung ke atas) ---
        Column(
            modifier = Modifier
                .offset(y = (-60).dp) // Overlap ke atas gambar
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(WhiteCard)
                .padding(24.dp)
        ) {
            // Indikator Geser (Handle Bar)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(50.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.LightGray.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // TAG KATEGORI & STATUS (Pill Shape)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CandyTag(
                    text = entri.category.uppercase(),
                    bgColor = CottonCandyBlue.copy(alpha = 0.1f),
                    textColor = CottonCandyBlue
                )

                // Logic warna status
                val statusColor = when(entri.status.lowercase()) {
                    "completed" -> ColorCompleted
                    "dropped" -> ColorDropped
                    "in_progress" -> ColorInProgress
                    else -> ColorPlanned
                }

                CandyTag(
                    text = entri.status.replace("_", " "),
                    bgColor = statusColor.copy(alpha = 0.1f),
                    textColor = statusColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // JUDUL UTAMA
            Text(
                text = entri.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black, // Lebih tebal
                color = Color.Black,
                lineHeight = 36.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // INFO BAR (Kotak Informasi Lucu)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftBlueInput) // Warna biru sangat muda
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Info Rating
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Star, null, tint = StarYellow, modifier = Modifier.size(24.dp))
                    Text(text = "${entri.rating}/5.0", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    Text(text = "Rating", fontSize = 10.sp, color = Color.Gray)
                }

                // Garis Pemisah
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.LightGray))

                // Info Genre
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Category, null, tint = CottonCandyBlue, modifier = Modifier.size(24.dp))
                    Text(
                        text = if (entri.genre.length > 10) entri.genre.take(10) + "..." else entri.genre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(text = "Genre", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DESKRIPSI HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Description, null, tint = CottonCandyBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sinopsis", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ISI DESKRIPSI
            Text(
                text = entri.description,
                fontSize = 15.sp,
                color = Color.DarkGray,
                lineHeight = 26.sp,
                textAlign = TextAlign.Justify
            )

            // Ruang kosong di bawah agar tombol FAB tidak menutupi teks terakhir
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// Komponen Tag yang Lebih Lucu
@Composable
fun CandyTag(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50)) // Bulat penuh (Pill)
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}