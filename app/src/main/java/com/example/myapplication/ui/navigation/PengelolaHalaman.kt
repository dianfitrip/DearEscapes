package com.example.myapplication.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.repository.UserPreferences
import com.example.myapplication.ui.view.HalamanWelcome
import com.example.myapplication.ui.view.auth.HalamanLogin
import com.example.myapplication.ui.view.auth.HalamanRegister
import com.example.myapplication.ui.view.detail.HalamanDetail
import com.example.myapplication.ui.view.entry.HalamanEntry
import com.example.myapplication.ui.view.entry.HalamanUpdate
import com.example.myapplication.ui.view.main.HalamanHome
import com.example.myapplication.ui.view.profile.HalamanEditProfile
import kotlinx.coroutines.flow.first
import com.example.myapplication.ui.navigation.*
import kotlinx.coroutines.launch






@Composable
fun NavGraph(
    userPreferences: UserPreferences,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    //CEK LOGIN SAAT APP START
    LaunchedEffect(Unit) {
        val userId = userPreferences.getUserId.first()
        startDestination = if (userId != null) {
            DestinasiHome.route
        } else {
            DestinasiWelcome.route
        }
    }

    //TUNGGU SAMPAI START DESTINATION DITENTUKAN
    if (startDestination == null) return

    NavHost(
        navController = navController,
        startDestination = startDestination!!,
        modifier = modifier
    ) {

        // WELCOME
        composable(DestinasiWelcome.route) {
            HalamanWelcome(
                onNextClick = {
                    navController.navigate(DestinasiLogin.route)
                }
            )
        }

        // REGISTER
        composable(DestinasiRegister.route) {
            HalamanRegister(
                onLoginClick = {
                    navController.navigate(DestinasiLogin.route)
                }
            )
        }

        // LOGIN
        composable(DestinasiLogin.route) {
            HalamanLogin(
                onRegisterClick = {
                    navController.navigate(DestinasiRegister.route)
                },
                onLoginSuccess = {
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // HOME
        composable(DestinasiHome.route) {
            HalamanHome(
                onDetailClick = { id ->
                    navController.navigate("${DestinasiDetail.route}/$id")
                },
                onAddClick = {
                    navController.navigate(DestinasiEntry.route)
                },
                onLogout = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEditProfileClick = {
                    navController.navigate(DestinasiEditProfil.route)
                }
            )
        }

        // ENTRY
        composable(DestinasiEntry.route) {
            HalamanEntry(
                navigateBack = { navController.popBackStack() }
            )
        }

        // DETAIL
        composable(
            route = DestinasiDetail.routeWithArg,
            arguments = listOf(navArgument(DestinasiDetail.idArg) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiDetail.idArg) ?: 0
            HalamanDetail(
                id = id,
                navigateBack = { navController.popBackStack() },
                navigateToEdit = { editId ->
                    navController.navigate("${DestinasiUpdate.route}/$editId")
                }
            )
        }

        // UPDATE
        composable(
            route = DestinasiUpdate.routeWithArg,
            arguments = listOf(navArgument(DestinasiUpdate.idArg) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiUpdate.idArg) ?: 0
            HalamanUpdate(
                id = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        // EDIT PROFIL
        composable(DestinasiEditProfil.route) {
            HalamanEditProfile(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
