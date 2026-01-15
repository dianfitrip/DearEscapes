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
import com.example.myapplication.ui.theme.*
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

    // State Error
    var isNameError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }

    // ===== PASSWORD VALIDATION (BARU) =====
    val isPasswordLengthError = password.isNotEmpty() && password.length != 6
    val isPasswordNoLetter = password.isNotEmpty() && !password.any { it.isLetter() }
    val isPasswordNoDigit = password.isNotEmpty() && !password.any { it.isDigit() }
    val isPasswordInvalid = isPasswordLengthError || isPasswordNoLetter || isPasswordNoDigit

    val registerStatus = viewModel.registerStatus

    LaunchedEffect(registerStatus) {
        if (registerStatus.contains("Berhasil")) {
            Toast.makeText(
                context,
                "Hore! Akun berhasil dibuat. Silakan Login.",
                Toast.LENGTH_LONG
            ).show()
            onLoginClick()
        } else if (registerStatus.contains("Gagal") || registerStatus.contains("Error")) {
            Toast.makeText(context, registerStatus, Toast.LENGTH_SHORT).show()
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Card(
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Buat Akun Baru",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = CottonCandyBlue
                    )

                    Text(
                        "Mulai jejak hiburanmu sekarang!",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // ===== NAMA =====
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            isNameError = !it.all { ch -> ch.isLetter() || ch.isWhitespace() }
                        },
                        label = { Text("Nama Lengkap") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                null,
                                tint = if (isNameError) ColorDropped else CottonCandyBlue
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = cuteInputColorsReg(isNameError),
                        isError = isNameError,
                        singleLine = true,
                        supportingText = {
                            if (isNameError) {
                                Text(
                                    "Nama tidak boleh mengandung angka atau simbol",
                                    color = ColorDropped,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // ===== EMAIL =====
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            isEmailError =
                                it.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                null,
                                tint = if (isEmailError) ColorDropped else CottonCandyBlue
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = cuteInputColorsReg(isEmailError),
                        isError = isEmailError,
                        singleLine = true,
                        supportingText = {
                            if (isEmailError) {
                                Text(
                                    "Format email tidak valid (contoh: x@gmail.com)",
                                    color = ColorDropped,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // ===== PASSWORD (KOMBINASI) =====
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            if (it.length <= 6) password = it
                        },
                        label = { Text("Password (6 Karakter)") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                null,
                                tint = if (isPasswordInvalid) ColorDropped else CottonCandyBlue
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(Icons.Default.Info, null, tint = Color.Gray)
                            }
                        },
                        colors = cuteInputColorsReg(isPasswordInvalid),
                        isError = isPasswordInvalid,
                        singleLine = true,
                        supportingText = {
                            when {
                                isPasswordLengthError ->
                                    Text("Password harus tepat 6 karakter", color = ColorDropped, fontSize = 12.sp)
                                isPasswordNoLetter ->
                                    Text("Password harus mengandung huruf", color = ColorDropped, fontSize = 12.sp)
                                isPasswordNoDigit ->
                                    Text("Password harus mengandung angka", color = ColorDropped, fontSize = 12.sp)
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // ===== KONFIRMASI PASSWORD =====
                    val isPasswordMatch =
                        password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
                    val isPasswordMismatch =
                        password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            if (it.length <= 6) confirmPassword = it
                        },
                        label = { Text("Ulangi Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = CottonCandyBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = cuteInputColorsReg(isPasswordMismatch),
                        singleLine = true,
                        isError = isPasswordMismatch,
                        trailingIcon = {
                            if (isPasswordMatch)
                                Icon(Icons.Default.CheckCircle, null, tint = ColorCompleted)
                            if (isPasswordMismatch)
                                Icon(Icons.Default.Warning, null, tint = ColorDropped)
                        }
                    )

                    if (isPasswordMismatch) {
                        Text(
                            "Password tidak cocok!",
                            color = ColorDropped,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    // ===== BUTTON DAFTAR =====
                    Button(
                        onClick = {
                            when {
                                name.isEmpty() || email.isEmpty() || password.isEmpty() ->
                                    Toast.makeText(context, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
                                isNameError ->
                                    Toast.makeText(context, "Nama tidak valid!", Toast.LENGTH_SHORT).show()
                                isEmailError ->
                                    Toast.makeText(context, "Email tidak valid!", Toast.LENGTH_SHORT).show()
                                isPasswordInvalid ->
                                    Toast.makeText(context, "Password harus 6 karakter dan kombinasi huruf & angka!", Toast.LENGTH_SHORT).show()
                                password != confirmPassword ->
                                    Toast.makeText(context, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                                else ->
                                    viewModel.register(name, email, password)
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
                            CircularProgressIndicator(
                                color = WhiteCard,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Daftar Sekarang",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sudah punya akun?", fontSize = 12.sp, color = Color.Gray)
                        TextButton(onClick = onLoginClick) {
                            Text(
                                "Masuk disini",
                                color = CottonCandyBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(50.dp))
        }
    }
}

/* ===== WARNA INPUT ===== */

@Composable
fun cuteInputColorsReg(isError: Boolean = false) =
    OutlinedTextFieldDefaults.colors(
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
fun DecorationItemReg(
    icon: ImageVector,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = modifier.size(size)
    )
}