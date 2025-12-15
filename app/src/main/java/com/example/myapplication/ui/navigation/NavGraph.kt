package com.example.myapplication.ui.navigation

composable(
route = "detail/{entertainmentId}",
arguments = listOf(navArgument("entertainmentId") { type = NavType.IntType })
) { backStackEntry ->
    val id = backStackEntry.arguments?.getInt("entertainmentId") ?: 0
    DetailScreen(entertainmentId = id, navController = navController)
}

composable(
route = "edit/{entertainmentId}",
arguments = listOf(navArgument("entertainmentId") { type = NavType.IntType })
) { backStackEntry ->
    val id = backStackEntry.arguments?.getInt("entertainmentId") ?: 0
    EditScreen(id = id, navController = navController)
}

composable("statistic") {
    StatisticScreen(navController = navController)
}