package com.example.myapplication.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R // Pastikan import R sesuai package Anda
import com.example.myapplication.ui.navigation.DestinasiNavigasi

object DestinasiWelcome : DestinasiNavigasi {
    override val route = "welcome"
    override val titleRes = "Welcome"
}

@Composable
fun HalamanWelcome(
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Menampilkan Gambar
        Image(
            painter = painterResource(id = R.drawable.logo_intro), // Pastikan nama file sesuai langkah no 1
            contentDescription = "Logo myapplication",
            modifier = Modifier
                .size(300.dp) // Sesuaikan ukuran gambar
                .padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )

        // 2. Teks Selamat Datang
        Text(
            text = "Selamat Datang di\nDEARESCAPES",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 3. Teks Deskripsi Singkat (Bla bla bla)
        Text(
            text = "Tempat terbaik untuk mencatat dan melacak jejak hiburanmu.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // 4. Tombol Lanjutkan
        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth(0.8f) // Lebar tombol 80% layar
        ) {
            Text(text = "Lanjutkan")
        }
    }
}