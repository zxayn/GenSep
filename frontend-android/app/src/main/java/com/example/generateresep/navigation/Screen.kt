package com.example.generateresep.navigation

sealed class Screen(val route: String) {
    data object Beranda : Screen("beranda")
    data object Catatan : Screen("catatan")
    data object Pengaturan : Screen("pengaturan")
}
