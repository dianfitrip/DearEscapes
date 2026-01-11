package com.example.myapplication.ui.view.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.myapplication.ui.viewmodel.LoginViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel // 1. Penting: Import Factory Provider

@Composable
fun HalamanLogin(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    // 2. Gunakan Factory agar UserPreferences ter-inject otomatis
    viewModel: LoginViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current

    // State Input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Observe Status
    val loginStatus = viewModel.loginStatus

    // 3. Logic Toast (Feedback UI)
    LaunchedEffect(loginStatus) {
        if (loginStatus.contains("Berhasil")) {
            Toast.makeText(context, loginStatus, Toast.LENGTH_SHORT).show()
            // Note: Navigasi dipindahkan ke tombol agar lebih pasti urutannya
        } else if (loginStatus.contains("Gagal") || loginStatus.contains("Error")) {
            Toast.makeText(context, loginStatus, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- HEADER ---
        Text(
            text = "Selamat Datang Kembali",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = CottonCandyBlue,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Masuk untuk melanjutkan catatan hiburanmu",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // --- INPUT EMAIL ---
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

        // --- INPUT PASSWORD ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
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

        Spacer(modifier = Modifier.height(32.dp))

        // --- TOMBOL LOGIN ---
        Button(
            onClick = {
                // 4. Panggil Login dengan 3 Parameter (email, password, callback sukses)
                viewModel.login(
                    email = email,
                    passInput = password,
                    onSuccess = {
                        // Navigasi ini hanya jalan jika session User ID sudah berhasil disimpan di ViewModel
                        onLoginSuccess()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue),
            shape = RoundedCornerShape(16.dp),
            enabled = loginStatus != "Loading..."
        ) {
            if (loginStatus == "Loading...") {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Masuk", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LINK KE REGISTER ---
        TextButton(onClick = onRegisterClick) {
            Text(text = "Belum punya akun? Daftar disini", color = CottonCandyBlue)
        }
    }
}