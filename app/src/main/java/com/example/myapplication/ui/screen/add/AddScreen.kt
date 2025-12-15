package com.example.myapplication.ui.screen.add


import android.content.Context
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
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.dianfitri.dearescapes.ui.common.UiState
import com.dianfitri.dearescapes.ui.common.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavController,
    viewModel: AddViewModel = viewModel(factory = ViewModelFactory())
) {
    val context = LocalContext.current
    val uploadState by viewModel.uploadState.collectAsState()

    // State untuk Form
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }

    // State untuk Pilihan (Category & Status)
    var selectedCategory by remember { mutableStateOf("watch") } // Default 'watch'
    var selectedStatus by remember { mutableStateOf("planned") }
    var statusExpanded by remember { mutableStateOf(false) } // Dropdown state
    val statusOptions = listOf("planned", "in_progress", "completed", "dropped")

    // State untuk Gambar
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher Galeri (Photo Picker)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        currentImageUri = uri
    }

    // Scrollable Content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tambah Hiburan Baru", style = MaterialTheme.typography.headlineMedium)

        // 1. Upload Gambar UI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            contentAlignment = Alignment.Center
        ) {
            if (currentImageUri != null) {
                AsyncImage(
                    model = currentImageUri,
                    contentDescription = "Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(48.dp))
                    Text("Pilih Gambar Sampul")
                }
            }
        }

        // 2. Input Fields
        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Judul (Title)") }, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description, onValueChange = { description = it },
            label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = genre, onValueChange = { genre = it },
            label = { Text("Genre (Misal: Horror, Action)") }, modifier = Modifier.fillMaxWidth()
        )

        // 3. Radio Button (Category)
        Text("Kategori:", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedCategory == "watch", onClick = { selectedCategory = "watch" })
            Text("Tontonan (Watch)")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = selectedCategory == "read", onClick = { selectedCategory = "read" })
            Text("Bacaan (Read)")
        }

        // 4. Dropdown (Status)
        ExposedDropdownMenuBox(
            expanded = statusExpanded,
            onExpandedChange = { statusExpanded = !statusExpanded }
        ) {
            OutlinedTextField(
                value = selectedStatus,
                onValueChange = {},
                readOnly = true,
                label = { Text("Status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                statusOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedStatus = selectionOption
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = rating, onValueChange = { rating = it },
            label = { Text("Rating (0.0 - 5.0)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // 5. Submit Button
        Button(
            onClick = {
                viewModel.uploadEntertainment(
                    context, title, description, genre, selectedCategory, selectedStatus, rating, currentImageUri
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotEmpty() && uploadState !is UiState.Loading
        ) {
            Text(if (uploadState is UiState.Loading) "Mengupload..." else "Simpan Data")
        }

        // Handle State Upload
        when (val state = uploadState) {
            is UiState.Success -> {
                LaunchedEffect(state) {
                    Toast.makeText(context, "Berhasil Menambah Data!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack() // Kembali ke Home
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
}