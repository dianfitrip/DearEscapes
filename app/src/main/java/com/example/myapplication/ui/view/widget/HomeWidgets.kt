package com.example.myapplication.ui.view.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.ui.theme.CottonCandyBlue
import kotlin.math.abs

// --- 1. BOTTOM NAVIGATION BAR ---
@Composable
fun CottonCandyBottomBar(
    currentMenu: String,
    onMenuSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
            Triple("Search", Icons.Filled.Search, Icons.Outlined.Search),
            Triple("Statistik", Icons.Filled.PieChart, Icons.Outlined.PieChart),
            Triple("Profil", Icons.Filled.Person, Icons.Outlined.Person)
        )

        items.forEach { (title, selectedIcon, unselectedIcon) ->
            NavigationBarItem(
                selected = currentMenu == title,
                onClick = { onMenuSelected(title) },
                icon = {
                    Icon(
                        imageVector = if (currentMenu == title) selectedIcon else unselectedIcon,
                        contentDescription = title
                    )
                },
                label = { Text(title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CottonCandyBlue,
                    selectedTextColor = CottonCandyBlue,
                    indicatorColor = CottonCandyBlue.copy(alpha = 0.2f)
                )
            )
        }
    }
}

// --- 2. HEADER HOME ---
@Composable
fun HomeHeader(username: String) {
    val userIcon = getCuteAvatar(username)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CottonCandyBlue, Color(0xFFF5F9FF))
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = userIcon,
                        contentDescription = "Avatar",
                        tint = CottonCandyBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = username.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Your Quiet Escapes",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic
                )
            }
            Icon(
                imageVector = Icons.Rounded.BubbleChart,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(42.dp)
                    .padding(end = 4.dp)
            )
        }
    }
}

// --- 3. POSTER CARD ---
@Composable
fun PosterCard(item: EntriHiburan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable { onClick() }
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageUrl = when {
                item.photo.isNullOrEmpty() -> ""
                item.photo.startsWith("http") ||
                        item.photo.startsWith("content://") ||
                        item.photo.startsWith("file://") -> item.photo
                else -> "http://10.0.2.2:3000/${item.photo}"
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = rememberVectorPainter(Icons.Default.BrokenImage),
                placeholder = rememberVectorPainter(Icons.Default.Image)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            Surface(
                color = CottonCandyBlue,
                shape = RoundedCornerShape(bottomEnd = 12.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = item.status,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.rating}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "|  ${item.category}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Logic Helper
@Composable
fun getCuteAvatar(username: String): ImageVector {
    val icons = listOf(
        Icons.Rounded.Face, Icons.Rounded.Pets, Icons.Rounded.Cloud,
        Icons.Rounded.Star, Icons.Rounded.Favorite, Icons.Rounded.Spa,
        Icons.Rounded.AcUnit, Icons.Rounded.AutoAwesome, Icons.Rounded.EmojiNature,
        Icons.Rounded.WbSunny
    )
    val index = abs(username.hashCode()) % icons.size
    return icons[index]
}