package com.example.generateresep.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.generateresep.beranda.BerandaScreen
import com.example.generateresep.catatan.CatatanScreen
import com.example.generateresep.login.LoginScreen
import com.example.generateresep.pengaturan.PengaturanScreen
import com.example.generateresep.register.RegisterScreen
import com.example.generateresep.resep.DetailResepScreen
import com.example.generateresep.viewmodel.AuthViewModel
import com.example.generateresep.viewmodel.RecipeViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    // 🌟 KUNCI PERBAIKAN: Kita buat ViewModel Resep di sini agar bisa di-share ke banyak layar
    val sharedRecipeViewModel: RecipeViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginClick = {
                    navController.navigate(Screen.Beranda.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Beranda.route) {
            // Gunakan shared ViewModel di sini
            BerandaScreen(
                viewModel = sharedRecipeViewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.DetailResep.createRoute(recipeId))
                }
            )
        }
        composable(
            route = Screen.DetailResep.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            // Gunakan shared ViewModel yang SAMA persis di sini
            DetailResepScreen(
                recipeId = recipeId,
                onBackClick = { navController.popBackStack() },
                viewModel = sharedRecipeViewModel
            )
        }
        composable(Screen.Catatan.route) {
            CatatanScreen()
        }
        composable(Screen.Pengaturan.route) {
            PengaturanScreen()
        }
    }
}