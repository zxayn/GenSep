package com.example.generateresep.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val sharedRecipeViewModel: RecipeViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) },
        popEnterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) },
        popExitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) }
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
        composable(
            route = Screen.Beranda.route,
            enterTransition = {
                if (initialState.destination.route == Screen.Login.route || initialState.destination.route == Screen.Register.route) {
                    scaleIn(animationSpec = tween(600), initialScale = 0.8f) + fadeIn(animationSpec = tween(600))
                } else {
                    fadeIn(animationSpec = tween(400)) + slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400))
                }
            }
        ) {
            BerandaScreen(
                viewModel = sharedRecipeViewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.DetailResep.createRoute(recipeId))
                }
            )
        }
        composable(
            route = Screen.DetailResep.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType }),
            enterTransition = {
                slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
            }
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
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
            val authViewModel: AuthViewModel = hiltViewModel()
            PengaturanScreen(
                authViewModel = authViewModel,
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
