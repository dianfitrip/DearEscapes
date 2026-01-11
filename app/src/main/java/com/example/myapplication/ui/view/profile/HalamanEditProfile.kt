package com.example.myapplication.ui.view.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.EditProfileEvent
import com.example.myapplication.ui.viewmodel.EditProfileViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditProfile(
    navigateBack: () -> Unit,
    viewModel: EditProfileViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val state = viewModel.uiState
    val softBlue = Color(0xFFF5F9FF)

    // State untuk menampilkan Dialog Konfirmasi
    var showDialog by remember { mutableStateOf(false) }

    // Logic saat tombol simpan ditekan (hanya memunculkan dialog)
    fun onSaveClicked() {
        if (state.username.isNotBlank() && state.email.isNotBlank()) {
            showDialog = true
        } else {
            // Trigger validasi kosong di ViewModel jika perlu, atau biarkan UI handle
            viewModel.saveChanges {} // Panggil ini cuma buat trigger error message "wajib diisi"
        }
    }

    // Logic konfirmasi simpan (eksekusi update)
    fun onConfirmSave() {
        showDialog = false
        viewModel.saveChanges(navigateBack) // Setelah sukses, otomatis navigateBack (ke Profil)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Konfirmasi Perubahan") },
            text = { Text("Apakah kamu yakin ingin menyimpan perubahan pada profil ini?") },
            confirmButton = {
                Button(
                    onClick = { onConfirmSave() },
                    colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue)
                ) {
                    Text("Ya, Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profil", fontWeight = FontWeight.Bold, color = CottonCandyBlue) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) { // Tombol Back di pojok kiri atas
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = CottonCandyBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = softBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error Message
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Form Fields
            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.updateUiState(EditProfileEvent.UsernameChanged(it)) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CottonCandyBlue,
                    focusedLabelColor = CottonCandyBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.updateUiState(EditProfileEvent.EmailChanged(it)) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CottonCandyBlue,
                    focusedLabelColor = CottonCandyBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.updateUiState(EditProfileEvent.PasswordChanged(it)) },
                label = { Text("Password Baru (Opsional)") },
                placeholder = { Text("Isi jika ingin mengganti password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CottonCandyBlue,
                    focusedLabelColor = CottonCandyBlue
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Simpan (Sekarang memicu Dialog)
            Button(
                onClick = { onSaveClicked() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue),
                shape = RoundedCornerShape(50)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}