package com.example.myapplication.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.* // Import icon rounded
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.ui.theme.CottonCandyBlue
import com.example.myapplication.ui.viewmodel.HomeUiState
import com.example.myapplication.ui.viewmodel.HomeViewModel
import com.example.myapplication.ui.viewmodel.PenyediaViewModel
import kotlin.math.abs

// Warna tambahan tema
val SoftPink = Color(0xFFFFB7B2)
val SoftYellow = Color(0xFFFFF9C4)

@Composable
fun HalamanHome(
    onDetailClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // Ambil Username dari ViewModel
    val username by viewModel.currentUsername.collectAsState()

    // State menu aktif untuk Bottom Navigation
    var activeMenu by remember { mutableStateOf("Home") }
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getEntries()
    }

    Scaffold(
        // 1. HEADER PROFIL
        topBar = {
            if (activeMenu == "Home") {
                HomeHeader(username = username)
            }
        },

        // 2. BOTTOM NAVIGATION
        bottomBar = {
            CottonCandyBottomBar(
                currentMenu = activeMenu,
                onMenuSelected = { activeMenu = it }
            )
        },

        // FAB (Tombol Tambah)
        floatingActionButton = {
            if (activeMenu == "Home") {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = CottonCandyBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                }
            }
        },
        containerColor = Color(0xFFF5F9FF)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (activeMenu) {
                "Home" -> ContentHome(uiState, viewModel, onDetailClick)
                "Search" -> PlaceholderScreen("Fitur Pencarian")
                "Statistik" -> PlaceholderScreen("Fitur Statistik")
                "Profil" -> ProfilSingkatView(username)
            }
        }
    }
}

// --- LOGIC IKON LUCU (DETERMINISTIC RANDOM) ---
@Composable
fun getCuteAvatar(username: String): ImageVector {
    val icons = listOf(
        Icons.Rounded.Face,           // Wajah Senyum
        Icons.Rounded.Pets,           // Jejak Kaki
        Icons.Rounded.Cloud,          // Awan
        Icons.Rounded.Star,           // Bintang
        Icons.Rounded.Favorite,       // Hati
        Icons.Rounded.Spa,            // Bunga/Daun
        Icons.Rounded.AcUnit,         // Salju/Geometris
        Icons.Rounded.AutoAwesome,    // Kilauan
        Icons.Rounded.EmojiNature,    // Alam
        Icons.Rounded.WbSunny         // Matahari
    )

    val index = abs(username.hashCode()) % icons.size
    return icons[index]
}

// --- HEADER PROFIL (TOP BAR - VERSI GELEMBUNG AIR) ---
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
            // AVATAR ICON
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

            // NAMA & TAGLINE BARU
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = username.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.1f),
                            blurRadius = 4f
                        )
                    )
                )
                // --- TAGLINE ESTETIK ---
                Text(
                    text = "Your Quiet Escapes",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic
                )
            }

            // --- IKON HIASAN GELEMBUNG AIR ---
            Icon(
                imageVector = Icons.Rounded.BubbleChart, // Ikon Gelembung
                contentDescription = null, // Hiasan saja
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(42.dp) // Ukuran pas
                    .padding(end = 4.dp)
            )
        }
    }
}

// --- BOTTOM NAVIGATION BAR ---
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

// --- CONTENT HOME ---
@Composable
fun ContentHome(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onDetailClick: (Int) -> Unit
) {
    when (uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CottonCandyBlue)
            }
        }
        is HomeUiState.Success -> {
            if (uiState.data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada data hiburan.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.data) { item ->
                        PosterCard(item = item, onClick = { onDetailClick(item.id) })
                    }
                }
            }
        }
        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                    Button(
                        onClick = { viewModel.getEntries() },
                        colors = ButtonDefaults.buttonColors(containerColor = CottonCandyBlue)
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }
        }
    }
}

// --- POSTER CARD ---
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
                        contentDescription = "Rating",
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

// --- PLACEHOLDER & PROFIL VIEW ---
@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, color = Color.Gray)
        }
    }
}

@Composable
fun ProfilSingkatView(username: String) {
    val userIcon = getCuteAvatar(username)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = CottonCandyBlue.copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(3.dp, CottonCandyBlue)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = userIcon,
                    contentDescription = null,
                    tint = CottonCandyBlue,
                    modifier = Modifier.size(70.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Halo, $username!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CottonCandyBlue)

        // Tagline Profil
        Text(text = "Your Quiet Escapes", color = Color.Gray, fontStyle = FontStyle.Italic)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* TODO: Logic Logout */ },
            colors = ButtonDefaults.buttonColors(containerColor = SoftPink),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Keluar (Logout)", fontWeight = FontWeight.Bold)
        }
    }
}