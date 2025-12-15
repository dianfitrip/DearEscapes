package com.example.myapplication.ui.screen.edit


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.ui.common.UiState
import com.example.myapplication.ui.common.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    id: Int,
    navController: NavController,
    viewModel: EditViewModel = viewModel(factory = ViewModelFactory())
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    // Variable State Form
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("watch") }
    var selectedStatus by remember { mutableStateOf("planned") }
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    var oldPhotoUrl by remember { mutableStateOf<String?>(null) }

    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("planned", "in_progress", "completed", "dropped")

    // 1. Ambil Data Awal
    LaunchedEffect(id) {
        viewModel.getEntertainmentDetail(id)
    }

    // 2. Isi Form jika data berhasil diambil
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val data = (uiState as UiState.Success).data
            title = data.title
            description = data.description
            genre = data.genre
            rating = data.rating.toString()
            selectedCategory = data.category
            selectedStatus = data.status
            oldPhotoUrl = data.photoUrl
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        currentImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Data") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gambar (Bisa pakai gambar baru atau gambar lama)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (currentImageUri != null) {
                    AsyncImage(model = currentImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else if (oldPhotoUrl != null) {
                    AsyncImage(model = oldPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Text("Ganti Foto")
                    }
                }
            }

            // Input Fields
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = genre, onValueChange = { genre = it }, label = { Text("Genre") }, modifier = Modifier.fillMaxWidth())

            // Category Radio
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedCategory == "watch", onClick = { selectedCategory = "watch" })
                Text("Watch")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = selectedCategory == "read", onClick = { selectedCategory = "read" })
                Text("Read")
            }

            // Status Dropdown
            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded }) {
                OutlinedTextField(
                    value = selectedStatus, onValueChange = {}, readOnly = true, label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { selectedStatus = option; statusExpanded = false })
                    }
                }
            }

            OutlinedTextField(value = rating, onValueChange = { rating = it }, label = { Text("Rating") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())

            // Tombol Update
            Button(
                onClick = {
                    viewModel.updateEntertainment(context, id, title, description, genre, selectedCategory, selectedStatus, rating, currentImageUri)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotEmpty()
            ) {
                Text(if (updateState is UiState.Loading && title.isNotEmpty()) "Menyimpan..." else "Update Data")
            }
        }
    }

    // Handle Hasil Update
    when (val state = updateState) {
        is UiState.Success -> {
            LaunchedEffect(state) {
                Toast.makeText(context, "Berhasil Update!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Kembali ke detail
                viewModel.resetUpdateState()
            }
        }
        is UiState.Error -> {
            LaunchedEffect(state) {
                Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        else -> {}
    }
}