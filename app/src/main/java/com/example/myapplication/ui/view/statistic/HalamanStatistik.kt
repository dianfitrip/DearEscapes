package com.example.myapplication.ui.view.statistic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // [PENTING] Import untuk rata tengah
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.*
import com.example.myapplication.ui.viewmodel.PenyediaViewModel
import com.example.myapplication.ui.viewmodel.StatistikUiState
import com.example.myapplication.ui.viewmodel.StatistikViewModel

@Composable
fun HalamanStatistik(
    viewModel: StatistikViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.fetchStatistik()
    }

    val uiState = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // HEADER
        Text(
            text = "Insight Hiburanmu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CottonCandyBlue
        )
        Text(
            text = "Seberapa sering kamu healing?",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CONTENT
        when (uiState) {
            is StatistikUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CottonCandyBlue)
                }
            }
            is StatistikUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = Color.Red)
                }
            }
            is StatistikUiState.Success -> {
                val data = uiState.data
                if (data.totalEntry == 0) {
                    EmptyStatView()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        // 1. LINE CHART: MENONTON VS MEMBACA
                        item {
                            ChartCard(title = "Menonton vs Membaca") {
                                val watchCount = data.categoryDistribution["watch"] ?: 0
                                val readCount = data.categoryDistribution["read"] ?: 0

                                ActivityLineChart(
                                    watchCount = watchCount,
                                    readCount = readCount
                                )
                            }
                        }

                        // 2. KARTU RINGKASAN ATAS
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Total Entri",
                                    value = data.totalEntry.toString(),
                                    icon = Icons.Rounded.Analytics,
                                    color = CottonCandyBlue
                                )
                                SummaryCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Rata-rata Rating",
                                    value = String.format("%.1f", data.averageRating),
                                    icon = Icons.Rounded.Star,
                                    color = PastelYellow
                                )
                            }
                        }

                        // 3. GRAFIK GENRE (Bar Chart)
                        item {
                            ChartCard(title = "Genre Terfavorit") {
                                Column {
                                    data.genreDistribution.entries.take(4).forEachIndexed { index, entry ->
                                        val color = PastelColors[index % PastelColors.size]
                                        val percentage = if (data.totalEntry > 0) entry.value.toFloat() / data.totalEntry.toFloat() else 0f

                                        GenreBarItem(
                                            label = entry.key,
                                            count = entry.value,
                                            percentage = percentage,
                                            color = color
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }

                        // 4. GRAFIK STATUS
                        item {
                            ChartCard(title = "Status Menonton/Membaca") {
                                Column {
                                    StatusItem("Planned", data.statusDistribution["planned"] ?: 0, ColorPlanned)
                                    StatusItem("In Progress", data.statusDistribution["in_progress"] ?: 0, ColorInProgress)
                                    StatusItem("Completed", data.statusDistribution["completed"] ?: 0, ColorCompleted)
                                    StatusItem("Dropped", data.statusDistribution["dropped"] ?: 0, ColorDropped)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- KOMPONEN CHART CUSTOM ---

@Composable
fun ActivityLineChart(watchCount: Int, readCount: Int) {
    val total = (watchCount + readCount).coerceAtLeast(1)
    val painter = rememberVectorPainter(Icons.Rounded.Pets)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 20.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val spacing = width / 3

                val p1 = Offset(spacing, height - (watchCount.toFloat() / total * height * 0.7f) - 30f)
                val p2 = Offset(spacing * 2, height - (readCount.toFloat() / total * height * 0.7f) - 30f)

                drawPath(
                    path = Path().apply {
                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                    },
                    color = CottonCandyBlue.copy(alpha = 0.5f),
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                )

                val iconSizePx = 28.dp.toPx()

                translate(left = p1.x - iconSizePx / 2, top = p1.y - iconSizePx / 2) {
                    with(painter) {
                        draw(
                            size = androidx.compose.ui.geometry.Size(iconSizePx, iconSizePx),
                            colorFilter = ColorFilter.tint(PastelPink)
                        )
                    }
                }

                translate(left = p2.x - iconSizePx / 2, top = p2.y - iconSizePx / 2) {
                    with(painter) {
                        draw(
                            size = androidx.compose.ui.geometry.Size(iconSizePx, iconSizePx),
                            colorFilter = ColorFilter.tint(PastelBlue)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$watchCount", fontWeight = FontWeight.Bold, color = PastelPink, fontSize = 20.sp)
                Text(text = "Menonton", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$readCount", fontWeight = FontWeight.Bold, color = PastelBlue, fontSize = 20.sp)
                Text(text = "Membaca", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

// [MODIFIKASI] Update ChartCard agar judulnya rata tengah
@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CottonCandyBlue,
                modifier = Modifier.fillMaxWidth(), // Agar bisa di-center
                textAlign = TextAlign.Center // Rata Tengah
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun GenreBarItem(label: String, count: Int, percentage: Float, color: Color) {
    val animatedWidth by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000),
        label = "BarWidth"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
            Text(text = "$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF0F0F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

@Composable
fun StatusItem(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color.Gray)
        Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun EmptyStatView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.Analytics, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Belum ada data statistik", color = Color.Gray)
    }
}