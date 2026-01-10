package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.view.HalamanWelcome
import com.example.myapplication.ui.view.auth.HalamanLogin
import com.example.myapplication.ui.view.auth.HalamanRegister
import com.example.myapplication.ui.view.entry.HalamanEntry
import com.example.myapplication.ui.view.home.HalamanHome

// --- 1. DEFINISI DESTINASI ---

interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

object DestinasiWelcome : DestinasiNavigasi {
    override val route = "welcome"
    override val titleRes = "Welcome"
}

object DestinasiRegister : DestinasiNavigasi {
    override val route = "register"
    override val titleRes = "Daftar"
}

object DestinasiLogin : DestinasiNavigasi {
    override val route = "login"
    override val titleRes = "Masuk"
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Home"
}

// Tambahan untuk tombol "Tambah Data" di Home
object DestinasiEntry : DestinasiNavigasi {
    override val route = "entry_hiburan"
    override val titleRes = "Tambah Hiburan"
}

// Tambahan untuk klik item di Home (Detail)
object DestinasiDetail : DestinasiNavigasi {
    override val route = "detail_hiburan"
    override val titleRes = "Detail Hiburan"
    const val idArg = "id_hiburan" // Argument ID untuk navigasi
    val routeWithArg = "$route/{$idArg}"
}

// --- 2. PENGELOLA NAVIGASI (NAVGRAPH) ---

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiWelcome.route,
        modifier = modifier
    ) {
        // --- HALAMAN WELCOME ---
        composable(route = DestinasiWelcome.route) {
            HalamanWelcome(
                onNextClick = {
                    navController.navigate(DestinasiLogin.route)
                }
            )
        }

        // --- HALAMAN REGISTER ---
        composable(route = DestinasiRegister.route) {
            HalamanRegister(
                onLoginClick = {
                    navController.navigate(DestinasiLogin.route)
                }
            )
        }

        // --- HALAMAN LOGIN ---
        composable(route = DestinasiLogin.route) {
            HalamanLogin(
                onRegisterClick = {
                    navController.navigate(DestinasiRegister.route)
                },
                onLoginSuccess = {
                    // Masuk ke Home dan hapus history agar tidak bisa back ke login
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(DestinasiWelcome.route) { inclusive = true }
                    }
                }
            )
        }

        // --- HALAMAN HOME ---
        composable(route = DestinasiHome.route) {
            HalamanHome(
                onDetailClick = { id ->
                    // Navigasi ke Detail membawa ID
                    navController.navigate("${DestinasiDetail.route}/$id")
                },
                onAddClick = {
                    // Navigasi ke Halaman Entry (Tambah Data)
                    navController.navigate(DestinasiEntry.route)
                }
            )
        }

        // --- HALAMAN ENTRY (SUDAH DIPERBARUI) ---
        composable(route = DestinasiEntry.route) {
            HalamanEntry(
                navigateBack = {
                    // Kembali ke halaman sebelumnya (Home) setelah simpan atau tekan back
                    navController.popBackStack()
                }
            )
        }

        // --- HALAMAN DETAIL (Placeholder / Persiapan) ---
        composable(
            route = DestinasiDetail.routeWithArg,
            arguments = listOf(navArgument(DestinasiDetail.idArg) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiDetail.idArg)

            // PERBAIKAN: Gunakan 'Box' dari foundation, bukan material3
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Halaman Detail ID: $id (Belum Dibuat)")
            }
        }
    }
}