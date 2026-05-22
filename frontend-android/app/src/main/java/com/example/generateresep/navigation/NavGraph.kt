package com.example.generateresep.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.generateresep.beranda.BerandaScreen
import com.example.generateresep.catatan.CatatanScreen
import com.example.generateresep.pengaturan.PengaturanScreen

import com.example.generateresep.login.LoginScreen
import com.example.generateresep.register.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
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
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterClick = {
                    // Sementara arahkan ke Login setelah register
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Beranda.route) {
            BerandaScreen()
        }
        composable(Screen.Catatan.route) {
            CatatanScreen()
        }
        composable(Screen.Pengaturan.route) {
            PengaturanScreen()
        }
    }
}
