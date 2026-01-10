package com.example.myapplication.ui.view.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.theme.SoftBlueInput
import com.example.myapplication.ui.viewmodel.RegisterViewModel

@Composable
fun HalamanRegister(
    onLoginClick: () -> Unit,
    // ViewModel di-inject di sini agar bisa memanggil fungsi register
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State untuk input data
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State untuk menyembunyikan/menampilkan password
    var passwordVisible by remember { mutableStateOf(false) }

    // Mengamati status dari ViewModel (Loading, Berhasil, atau Gagal)
    val registerStatus = viewModel.registerStatus

    // Efek samping: Memunculkan Toast jika status berubah
    LaunchedEffect(registerStatus) {
        if (registerStatus.contains("Berhasil")) {
            Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
            onLoginClick() // Otomatis pindah ke halaman login jika sukses
        } else if (registerStatus.contains("Gagal") || registerStatus.contains("Error")) {
            Toast.makeText(context, registerStatus, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState), // Agar bisa discroll jika keyboard muncul
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- HEADER ---
        Text(
            text = "Buat Akun Baru",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CottonCandyBlue,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Daftar untuk mulai mencatat jejak hiburanmu",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // --- FORM INPUT ---

        // 1. Nama Lengkap
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CottonCandyBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = CottonCandyBlue,
                unfocusedContainerColor = SoftBlueInput.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CottonCandyBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = CottonCandyBlue,
                unfocusedContainerColor = SoftBlueInput.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CottonCandyBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = CottonCandyBlue,
                unfocusedContainerColor = SoftBlueInput.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Konfirmasi Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CottonCandyBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = CottonCandyBlue,
                unfocusedContainerColor = SoftBlueInput.copy(alpha = 0.3f)
            ),
            singleLine = true,
            isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword,
            supportingText = {
                if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Text(text = "Password tidak cocok", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- TOMBOL DAFTAR ---
        Button(
            onClick = {
                // Validasi sederhana sebelum kirim ke server
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    if (password == confirmPassword) {
                        // Panggil ViewModel untuk menembak API
                        viewModel.register(name, email, password)
                    } else {
                        Toast.makeText(context, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CottonCandyBlue
            ),
            shape = RoundedCornerShape(16.dp),
            // Matikan tombol jika sedang loading
            enabled = registerStatus != "Loading..."
        ) {
            if (registerStatus == "Loading...") {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Daftar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Menampilkan pesan status di bawah tombol (Opsional)
        if (registerStatus.isNotEmpty() && registerStatus != "Loading...") {
            Text(
                text = registerStatus,
                modifier = Modifier.padding(top = 8.dp),
                color = if (registerStatus.contains("Berhasil")) CottonCandyBlue else Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- FOOTER (LOGIN LINK) ---
        TextButton(onClick = onLoginClick) {
            Text(
                text = "Sudah punya akun? Masuk disini",
                color = CottonCandyBlue
            )
        }
    }
}