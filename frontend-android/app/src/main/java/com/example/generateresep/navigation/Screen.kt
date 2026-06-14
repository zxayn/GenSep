package com.example.generateresep.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Beranda : Screen("beranda")
    data object Catatan : Screen("catatan")
    data object Pengaturan : Screen("pengaturan")
    data object DetailResep : Screen("detail_resep/{recipeId}") {
        fun createRoute(recipeId: Int) = "detail_resep/$recipeId"
    }
}
