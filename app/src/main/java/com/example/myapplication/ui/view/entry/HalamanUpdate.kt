package com.example.myapplication.ui.view.entry

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.PenyediaViewModel
import com.example.myapplication.ui.viewmodel.UpdateViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanUpdate(
    id: Int,
    navigateBack: () -> Unit,
    viewModel: UpdateViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // State untuk Dialog Konfirmasi Update
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadEntri(id)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Hiburan", color = CottonCandyBlue, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
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
                    // Reuse EntryBody (sekarang sudah bersih dari dialog internal)
                    EntryBody(
                        entryUiState = viewModel.uiState,
                        onValueChange = viewModel::updateUiState,
                        onSaveClick = {
                            // Cek Validasi di SINI (Parent HalamanUpdate)
                            if (viewModel.uiState.isEntryValid) {
                                showConfirmDialog = true // Munculkan dialog UPDATE
                            } else {
                                Toast.makeText(context, "Mohon lengkapi Judul dan Deskripsi!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    // [DIALOG KONFIRMASI UPDATE - KHUSUS HALAMAN UPDATE]
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Update", fontWeight = FontWeight.Bold, color = CottonCandyBlue) },
            text = { Text("Apakah kamu yakin ingin merubah data ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        // Eksekusi Update
                        coroutineScope.launch {
                            viewModel.updateEntri(id, context, navigateBack)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue)
                ) {
                    Text("Ya, Ubah")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}