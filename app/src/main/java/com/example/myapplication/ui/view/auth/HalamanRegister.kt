package com.example.myapplication.ui.view.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.myapplication.ui.viewmodel.RegisterViewModel

@Composable
fun HalamanRegister(
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State Input
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // State Error Validasi
    var isNameError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) } // [BARU] Error Email
    var isPasswordLengthError by remember { mutableStateOf(false) }

    // Observe Status
    val registerStatus = viewModel.registerStatus

    LaunchedEffect(registerStatus) {
        if (registerStatus.contains("Berhasil")) {
            Toast.makeText(context, "Hore! Akun berhasil dibuat. Silakan Login.", Toast.LENGTH_LONG).show()
            onLoginClick()
        } else if (registerStatus.contains("Gagal") || registerStatus.contains("Error")) {
            Toast.makeText(context, registerStatus, Toast.LENGTH_SHORT).show()
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
        // --- DEKORASI ---
        DecorationItemReg(icon = Icons.Filled.Face, color = SoftBlueInput, size = 120.dp, modifier = Modifier.align(Alignment.TopStart).offset(x = (-30).dp, y = 20.dp))
        DecorationItemReg(icon = Icons.Filled.Star, color = CottonCandyBlue.copy(alpha = 0.3f), size = 50.dp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 80.dp))
        DecorationItemReg(icon = Icons.Filled.Favorite, color = SoftBlueInput, size = 40.dp, modifier = Modifier.align(Alignment.BottomStart).offset(x = 30.dp, y = (-50).dp))

        // --- KARTU FORM ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(20.dp))

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
                        text = "Buat Akun Baru",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = CottonCandyBlue
                    )
                    Text(
                        text = "Mulai jejak hiburanmu sekarang!",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // 1. INPUT NAMA (Validasi Karakter)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { input ->
                            name = input
                            isNameError = !input.all { it.isLetter() || it.isWhitespace() }
                        },
                        label = { Text("Nama Lengkap", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Person, null, tint = if (isNameError) ColorDropped else CottonCandyBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = cuteInputColorsReg(isError = isNameError),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = isNameError,
                        supportingText = {
                            if (isNameError) {
                                Text("Nama tidak boleh angka/simbol!", color = ColorDropped, fontSize = 12.sp)
                            }
                        },
                        trailingIcon = {
                            if (isNameError) Icon(Icons.Filled.Warning, null, tint = ColorDropped)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. INPUT EMAIL (Validasi Format Real-time)
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            // Validasi: Error jika tidak kosong DAN format salah
                            isEmailError = it.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                        },
                        label = { Text("Email", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Email, null, tint = if (isEmailError) ColorDropped else CottonCandyBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = cuteInputColorsReg(isError = isEmailError), // Warna merah jika error
                        singleLine = true,
                        isError = isEmailError,
                        supportingText = {
                            if (isEmailError) {
                                Text("Format email tidak valid! contoh x@gmail.com", color = ColorDropped, fontSize = 12.sp)
                            }
                        },
                        trailingIcon = {
                            if (isEmailError) Icon(Icons.Filled.Warning, null, tint = ColorDropped)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. INPUT PASSWORD (Max 6 Karakter)
                    isPasswordLengthError = password.isNotEmpty() && password.length < 6

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            if (it.length <= 6) {
                                password = it
                            }
                        },
                        label = { Text("Password (6 Karakter)", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = if (isPasswordLengthError) ColorDropped else CottonCandyBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        colors = cuteInputColorsReg(isError = isPasswordLengthError),
                        singleLine = true,
                        isError = isPasswordLengthError,
                        supportingText = {
                            if (isPasswordLengthError) {
                                Text("Password harus 6 karakter!", color = ColorDropped, fontSize = 12.sp)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. KONFIRMASI PASSWORD
                    val isPasswordMatch = password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
                    val isPasswordMismatch = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            if (it.length <= 6) {
                                confirmPassword = it
                            }
                        },
                        label = { Text("Ulangi Password", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = CottonCandyBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        colors = cuteInputColorsReg(),
                        singleLine = true,
                        isError = isPasswordMismatch,
                        trailingIcon = {
                            if (isPasswordMatch) Icon(Icons.Filled.CheckCircle, null, tint = ColorCompleted)
                            if (isPasswordMismatch) Icon(Icons.Filled.Warning, null, tint = ColorDropped)
                        }
                    )

                    if (isPasswordMismatch) {
                        Text(
                            text = "Password tidak cocok!",
                            color = ColorDropped,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- TOMBOL DAFTAR ---
                    Button(
                        onClick = {
                            // Cek Kelengkapan & Validasi
                            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(context, "Lengkapi semua data dulu ya!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (isNameError) {
                                Toast.makeText(context, "Nama mengandung simbol/angka!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (isEmailError) {
                                Toast.makeText(context, "Format email salah!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password.length != 6) {
                                Toast.makeText(context, "Password wajib 6 karakter!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password == confirmPassword) {
                                viewModel.register(name, email, password)
                            } else {
                                Toast.makeText(context, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(4.dp, RoundedCornerShape(50)),
                        colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue),
                        shape = RoundedCornerShape(50),
                        enabled = registerStatus != "Loading..."
                    ) {
                        if (registerStatus == "Loading...") {
                            CircularProgressIndicator(color = WhiteCard, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "Daftar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Link Login
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sudah punya akun?", fontSize = 12.sp, color = Color.Gray)
                        TextButton(onClick = onLoginClick) {
                            Text("Masuk disini", color = CottonCandyBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun cuteInputColorsReg(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
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
fun DecorationItemReg(icon: ImageVector, color: Color, size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = modifier.size(size))
}