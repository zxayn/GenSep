package com.example.generateresep.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.generateresep.beranda.BerandaScreen
import com.example.generateresep.catatan.CatatanScreen
import com.example.generateresep.pengaturan.PengaturanScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Beranda.route
    ) {
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
