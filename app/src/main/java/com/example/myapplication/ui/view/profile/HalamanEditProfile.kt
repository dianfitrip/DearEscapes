package com.example.myapplication.ui.view.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    val context = LocalContext.current
    val state = viewModel.uiState
    val softBlue = Color(0xFFF5F9FF)

    var showDialog by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf(state.password) }

    /* ================= EFFECTS ================= */

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(state.password) {
        passwordText = state.password
    }

    /* ================= ACTIONS ================= */

    fun onSaveClicked() {
        val isValid =
            state.usernameError == null &&
                    state.emailError == null &&
                    state.passwordError == null &&
                    state.username.isNotBlank() &&
                    state.email.isNotBlank()

        if (isValid) {
            showDialog = true
        } else {
            Toast.makeText(
                context,
                "Periksa kembali data yang dimasukkan",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onConfirmSave() {
        showDialog = false
        viewModel.saveChanges(navigateBack)
    }

    //dialog validasi

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Perubahan",
                    fontWeight = FontWeight.Bold,
                    color = CottonCandyBlue
                )
            },
            text = {
                Text(
                    text = "Apakah kamu yakin ingin menyimpan perubahan?",
                    fontSize = 16.sp
                )
            },
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
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    /* ================= UI ================= */

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profil",
                        fontWeight = FontWeight.Bold,
                        color = CottonCandyBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = CottonCandyBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
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

            /* ===== Nama ===== */

            OutlinedTextField(
                value = state.username,
                onValueChange = {
                    viewModel.updateUiState(EditProfileEvent.UsernameChanged(it))
                },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = state.usernameError != null,
                supportingText = {
                    state.usernameError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ===== Email ===== */

            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    viewModel.updateUiState(EditProfileEvent.EmailChanged(it))
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ===== Password ===== */

            OutlinedTextField(
                value = passwordText,
                onValueChange = {
                    if (it.length <= 6) {
                        passwordText = it
                        viewModel.updateUiState(EditProfileEvent.PasswordChanged(it))
                    }
                },
                label = { Text("Password Baru (Opsional)") },
                placeholder = { Text("Password Harus 6 karakter dengan kombinasi huruf dan angka") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = state.passwordError != null,
                supportingText = {
                    when {
                        state.passwordError != null -> {
                            Text(
                                text = state.passwordError,
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                        state.password.isNotBlank() -> {
                            Text(
                                text = "${state.password.length}/6 karakter",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            /* ===== Button ===== */

            Button(
                onClick = { onSaveClicked() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CottonCandyBlue,
                    disabledContainerColor = CottonCandyBlue.copy(alpha = 0.5f)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simpan Perubahan",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
