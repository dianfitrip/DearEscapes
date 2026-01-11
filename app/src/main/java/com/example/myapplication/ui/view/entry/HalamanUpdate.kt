package com.example.myapplication.ui.view.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

    // 1. Tambahkan State untuk Scroll
    val scrollState = rememberScrollState()

    // Load data lama saat halaman dibuka
    LaunchedEffect(id) {
        viewModel.loadEntri(id)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Hiburan", color = CottonCandyBlue) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CottonCandyBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F9FF) // Samakan background dengan HalamanEntry
    ) { innerPadding ->

        // 2. Tambahkan Column Utama dengan verticalScroll
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState) // <-- INI KUNCINYA AGAR BISA DISCROLL
                .padding(16.dp)
        ) {
            // 3. Bungkus dengan Card agar estetik (Sama seperti HalamanEntry)
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
                    // Kita pakai EntryBody yang sudah ada (Reuse Component)
                    EntryBody(
                        entryUiState = viewModel.uiState,
                        onValueChange = viewModel::updateUiState,
                        onSaveClick = {
                            coroutineScope.launch {
                                viewModel.updateEntri(id, context, navigateBack)
                            }
                        }
                    )
                }
            }
        }
    }
}