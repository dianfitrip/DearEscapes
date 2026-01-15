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
import kotlinx.coroutines.launch


// DEFINISI DESTINASI

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

object DestinasiEntry : DestinasiNavigasi {
    override val route = "entry_hiburan"
    override val titleRes = "Tambah Hiburan"
}

object DestinasiDetail : DestinasiNavigasi {
    override val route = "detail_hiburan"
    override val titleRes = "Detail Hiburan"
    const val idArg = "id_hiburan"
    val routeWithArg = "$route/{$idArg}"
}

object DestinasiUpdate : DestinasiNavigasi {
    override val route = "update_hiburan"
    override val titleRes = "Edit Hiburan"
    const val idArg = "id_update"
    val routeWithArg = "$route/{$idArg}"
}

object DestinasiEditProfil : DestinasiNavigasi {
    override val route = "edit_profil"
    override val titleRes = "Edit Profil"
}



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
