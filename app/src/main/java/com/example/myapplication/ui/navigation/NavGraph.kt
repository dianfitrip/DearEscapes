package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// --- PENTING: Import semua halaman screen di sini ---
import com.example.myapplication.ui.screen.add.AddScreen
import com.example.myapplication.ui.screen.auth.LoginScreen
import com.example.myapplication.ui.screen.auth.RegisterScreen
import com.example.myapplication.ui.screen.detail.DetailScreen
import com.example.myapplication.ui.screen.edit.EditScreen
import com.example.myapplication.ui.screen.home.HomeScreen
import com.example.myapplication.ui.screen.statistic.StatisticScreen

@Composable
fun NavGraph(
    navController: NavHostController // <-- Ini deklarasi navController agar tidak merah
) {
    // NavHost adalah wadah utama navigasi
    NavHost(
        navController = navController,
        startDestination = "login" // Halaman pertama yang muncul
    ) {
        // 1. Login
        composable("login") {
            LoginScreen(navController = navController)
        }

        // 2. Register
        composable("register") {
            RegisterScreen(navController = navController)
        }

        // 3. Home
        composable("home") {
            HomeScreen(navController = navController)
        }

        // 4. Add (Tambah Data)
        composable("add") {
            AddScreen(navController = navController)
        }

        // 5. Statistic
        composable("statistic") {
            StatisticScreen(navController = navController)
        }

        // 6. Detail (Menerima ID)
        composable(
            route = "detail/{entertainmentId}",
            arguments = listOf(navArgument("entertainmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("entertainmentId") ?: 0
            DetailScreen(entertainmentId = id, navController = navController)
        }

        // 7. Edit (Menerima ID)
        composable(
            route = "edit/{entertainmentId}",
            arguments = listOf(navArgument("entertainmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("entertainmentId") ?: 0
            EditScreen(id = id, navController = navController)
        }
    }
}