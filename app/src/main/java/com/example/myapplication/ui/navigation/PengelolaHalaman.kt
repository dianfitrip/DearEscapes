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

// Tambahan untuk Halaman Update/Edit
object DestinasiUpdate : DestinasiNavigasi {
    override val route = "update_hiburan"
    override val titleRes = "Edit Hiburan"
    const val idArg = "id_update"
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

        // --- HALAMAN ENTRY ---
        composable(route = DestinasiEntry.route) {
            HalamanEntry(
                navigateBack = {
                    // Kembali ke halaman sebelumnya (Home) setelah simpan atau tekan back
                    navController.popBackStack()
                }
            )
        }

        // --- HALAMAN DETAIL ---
        composable(
            route = DestinasiDetail.routeWithArg,
            arguments = listOf(navArgument(DestinasiDetail.idArg) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            // Ambil ID dari argumen navigasi (default 0 jika error)
            val id = backStackEntry.arguments?.getInt(DestinasiDetail.idArg) ?: 0

            // Panggil Halaman Detail yang sesungguhnya
            com.example.myapplication.ui.view.detail.HalamanDetail(
                id = id,
                navigateBack = { navController.popBackStack() },
                // MODIFIKASI: Tambahkan navigasi ke Halaman Update
                navigateToEdit = { editId ->
                    navController.navigate("${DestinasiUpdate.route}/$editId")
                }
            )
        }

        // --- HALAMAN UPDATE / EDIT ---
        composable(
            route = DestinasiUpdate.routeWithArg,
            arguments = listOf(navArgument(DestinasiUpdate.idArg) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiUpdate.idArg) ?: 0
            com.example.myapplication.ui.view.entry.HalamanUpdate(
                id = id,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}