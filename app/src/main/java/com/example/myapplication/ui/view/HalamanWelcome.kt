package com.example.myapplication.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.navigation.DestinasiNavigasi
// [PENTING] Import file Color.kt
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.theme.CottonCandyPink
import com.example.myapplication.ui.theme.SoftBlueInput

object DestinasiWelcome : DestinasiNavigasi {
    override val route = "welcome"
    override val titleRes = "Welcome"
}

@Composable
fun HalamanWelcome(
    onNextClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        CottonCandyBlue.copy(alpha = 0.3f),
                        CottonCandyBlue.copy(alpha = 0.6f)
                    )
                )
            )
    ) {
        // --- DEKORASI BACKGROUND (Awan & Bintang Lucu) ---
        // Menggunakan SoftBlueInput (pengganti PaleBlue) dan CottonCandyBlue (pengganti SoftBlue)
        DecorationItem(icon = Icons.Rounded.Cloud, color = SoftBlueInput, size = 120.dp, modifier = Modifier.align(Alignment.TopStart).offset(x = (-30).dp, y = 50.dp))
        DecorationItem(icon = Icons.Rounded.Cloud, color = SoftBlueInput, size = 150.dp, modifier = Modifier.align(Alignment.TopEnd).offset(x = 40.dp, y = 100.dp))
        DecorationItem(icon = Icons.Rounded.Star, color = CottonCandyBlue.copy(alpha = 0.4f), size = 40.dp, modifier = Modifier.align(Alignment.TopCenter).offset(y = 150.dp))
        DecorationItem(icon = Icons.Rounded.AutoAwesome, color = CottonCandyPink, size = 30.dp, modifier = Modifier.align(Alignment.CenterStart).offset(x = 40.dp, y = (-100).dp))

        // --- KONTEN UTAMA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Logo Container (Lingkaran dengan Shadow)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp)
                    .shadow(elevation = 10.dp, shape = CircleShape, spotColor = CottonCandyBlue)
                    .background(Color.White, shape = CircleShape)
                    .padding(20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_intro),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Teks Headline
            Text(
                text = "Welcome to\nDEARESCAPES",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                color = CottonCandyBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Teks Deskripsi
            Surface(
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Catat momen seru, simpan kenangan,\ndan temukan dunia hiburanmu disini! ✨",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 4. Tombol Utama
            Button(
                onClick = onNextClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CottonCandyBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Mulai Jelajahi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Rounded.ArrowForward, contentDescription = null)
            }
        }

        // Dekorasi Bawah
        DecorationItem(icon = Icons.Rounded.Cloud, color = Color.White.copy(alpha = 0.5f), size = 200.dp, modifier = Modifier.align(Alignment.BottomStart).offset(x = (-50).dp, y = 80.dp))
    }
}

// Komponen Helper untuk Dekorasi
@Composable
fun DecorationItem(
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