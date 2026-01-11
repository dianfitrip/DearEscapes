package com.example.myapplication.ui.view.detail

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.DetailUiState
import com.example.myapplication.ui.viewmodel.DetailViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel

// Warna Tambahan Tema
val SoftPink = Color(0xFFFFB7B2)
val StrongPink = Color(0xFFFF6B81)
val SoftBackground = Color(0xFFF5F9FF)
val StarYellow = Color(0xFFFFD700)

@Composable
fun HalamanDetail(
    id: Int,
    navigateBack: () -> Unit,
    // Parameter baru untuk navigasi ke halaman Edit
    navigateToEdit: (Int) -> Unit,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Ambil data detail saat ID berubah
    LaunchedEffect(id) {
        viewModel.getDetailHiburan(id)
    }

    val uiState = viewModel.uiState
    val currentEntry = (uiState as? DetailUiState.Success)?.entri

    // --- LOGIC DIALOG HAPUS ---
    if (showDeleteDialog && currentEntry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Hapus \"${currentEntry.title}\"? \uD83D\uDE22",
                    color = CottonCandyBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = { Text("Apakah kamu yakin ingin menghapus catatan ini selamanya? Data tidak bisa kembali lho.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEntri(
                            id = id,
                            onSuccess = {
                                Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                                navigateBack()
                            },
                            onError = { message ->
                                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StrongPink)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = CottonCandyBlue)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        // Floating Action Button untuk Edit
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Panggil fungsi navigasi ke halaman Edit dengan ID saat ini
                    navigateToEdit(id)
                },
                shape = CircleShape,
                containerColor = CottonCandyBlue,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Entri")
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
                            Icon(Icons.Rounded.SentimentDissatisfied, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(uiState.message, color = Color.Gray)
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
        // --- HEADER GAMBAR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
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

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                            startY = 600f
                        )
                    )
            )

            // TOMBOL BACK (Kiri Atas)
            IconButton(
                onClick = navigateBack,
                modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
                    .size(40.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = CottonCandyBlue)
            }

            // TOMBOL DELETE (Kanan Atas)
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
                    .size(40.dp)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = StrongPink)
            }
        }

        // --- LEMBAR KONTEN ---
        Column(
            modifier = Modifier
                .offset(y = (-50).dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            // Handle Bar
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // TAGS
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CandyTag(text = entri.category.uppercase(), color = CottonCandyBlue)
                CandyTag(text = entri.status, color = SoftPink)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // JUDUL
            Text(
                text = entri.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // INFO ROW
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, null, tint = StarYellow, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${entri.rating}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(text = "/5.0", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Rounded.Category, null, tint = CottonCandyBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = entri.genre, color = Color.Gray, fontSize = 14.sp)
            }

            Divider(modifier = Modifier.padding(vertical = 24.dp), color = SoftBackground)

            // DESKRIPSI
            Text(text = "Sinopsis", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CottonCandyBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entri.description,
                fontSize = 15.sp,
                color = Color.DarkGray,
                lineHeight = 24.sp,
                textAlign = TextAlign.Justify
            )

            // Spacer Bawah
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CandyTag(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}