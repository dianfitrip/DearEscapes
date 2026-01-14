package com.example.myapplication.ui.view.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.* // Import Color.kt
import com.example.myapplication.ui.viewmodel.LoginViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel

@Composable
fun HalamanLogin(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current

    // State Input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // State Error Validasi
    var isEmailError by remember { mutableStateOf(false) }
    var emailErrorMsg by remember { mutableStateOf("") }

    var isPasswordError by remember { mutableStateOf(false) }
    var passwordErrorMsg by remember { mutableStateOf("") }

    // Observe Status
    val loginStatus = viewModel.loginStatus

    LaunchedEffect(loginStatus) {
        if (loginStatus.contains("Berhasil")) {
            Toast.makeText(context, loginStatus, Toast.LENGTH_SHORT).show()
        } else if (loginStatus.contains("Gagal") || loginStatus.contains("Error")) {
            Toast.makeText(context, loginStatus, Toast.LENGTH_SHORT).show()
        }
    }

    // --- LAYOUT UTAMA ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WhiteCard,
                        CottonCandyBlue.copy(alpha = 0.4f),
                        CottonCandyBlue.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        // --- DEKORASI BACKGROUND ---
        DecorationItem(icon = Icons.Filled.Favorite, color = SoftBlueInput, size = 100.dp, modifier = Modifier.align(Alignment.TopStart).offset(x = (-20).dp, y = 40.dp))
        DecorationItem(icon = Icons.Filled.Favorite, color = SoftBlueInput.copy(alpha = 0.6f), size = 80.dp, modifier = Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = 100.dp))
        DecorationItem(icon = Icons.Filled.Star, color = CottonCandyBlue.copy(alpha = 0.3f), size = 40.dp, modifier = Modifier.align(Alignment.CenterStart).offset(x = 20.dp, y = (-150).dp))

        // --- KARTU FORM LOGIN ---
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hello Again!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = CottonCandyBlue
                    )
                    Text(
                        text = "Silakan masuk untuk melanjutkan",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // --- INPUT EMAIL ---
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            isEmailError = false
                        },
                        label = { Text("Email", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Email, null, tint = if (isEmailError) ColorDropped else CottonCandyBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = cuteInputColorsLogin(isError = isEmailError),
                        singleLine = true,
                        isError = isEmailError,
                        supportingText = {
                            if (isEmailError) {
                                Text(text = emailErrorMsg, color = ColorDropped, fontSize = 12.sp)
                            }
                        },
                        trailingIcon = {
                            if (isEmailError) Icon(Icons.Filled.Warning, null, tint = ColorDropped)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- INPUT PASSWORD (MAX 6 KARAKTER) ---
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            // [VALIDASI] Batasi input maksimal 6 karakter
                            if (it.length <= 6) {
                                password = it
                                isPasswordError = false
                            }
                        },
                        label = { Text("Password (6 Karakter)", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = if (isPasswordError) ColorDropped else CottonCandyBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        colors = cuteInputColorsLogin(isError = isPasswordError),
                        singleLine = true,
                        isError = isPasswordError,
                        supportingText = {
                            if (isPasswordError) {
                                Text(text = passwordErrorMsg, color = ColorDropped, fontSize = 12.sp)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- TOMBOL MASUK (TANPA ICON) ---
                    Button(
                        onClick = {
                            var valid = true

                            // 1. Validasi Email
                            if (email.isBlank()) {
                                isEmailError = true
                                emailErrorMsg = "Email wajib diisi!"
                                valid = false
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                isEmailError = true
                                emailErrorMsg = "Format email tidak valid!. contoh x@gmail.com"
                                valid = false
                            }

                            // 2. Validasi Password (WAJIB 6 KARAKTER)
                            if (password.length != 6) {
                                isPasswordError = true
                                passwordErrorMsg = "Password harus 6 karakter!"
                                valid = false
                            }

                            // 3. Eksekusi Login
                            if (valid) {
                                viewModel.login(
                                    email = email,
                                    passInput = password,
                                    onSuccess = { onLoginSuccess() }
                                )
                            } else {
                                Toast.makeText(context, "Periksa data Anda kembali", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(4.dp, RoundedCornerShape(50)),
                        colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue),
                        shape = RoundedCornerShape(50),
                        enabled = loginStatus != "Loading..."
                    ) {
                        if (loginStatus == "Loading...") {
                            CircularProgressIndicator(color = WhiteCard, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "Masuk Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Link Daftar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Belum punya akun?", fontSize = 12.sp, color = Color.Gray)
                        TextButton(onClick = onRegisterClick) {
                            Text("Daftar disini", color = CottonCandyBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Dekorasi Bawah
        DecorationItem(icon = Icons.Filled.Favorite, color = WhiteCard.copy(alpha = 0.3f), size = 150.dp, modifier = Modifier.align(Alignment.BottomStart).offset(x = (-40).dp, y = 60.dp))
    }
}

// Helper Warna Input
@Composable
fun cuteInputColorsLogin(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = if (isError) ColorDropped else CottonCandyBlue,
    unfocusedBorderColor = if (isError) ColorDropped else Color.LightGray,
    focusedLabelColor = if (isError) ColorDropped else CottonCandyBlue,
    focusedContainerColor = SoftBackground,
    unfocusedContainerColor = SoftBackground,
    errorBorderColor = ColorDropped,
    errorLabelColor = ColorDropped,
    errorCursorColor = ColorDropped
)

@Composable
fun DecorationItem(icon: ImageVector, color: Color, size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = modifier.size(size))
}