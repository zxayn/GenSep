package com.example.generateresep.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.generateresep.R

import com.example.generateresep.ui.theme.GreenMain
import com.example.generateresep.ui.theme.LightGreenBg

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(
        containerColor = LightGreenBg,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            NavigationItem("Beranda", R.drawable.icon_beranda, Screen.Beranda.route),
            NavigationItem("Catatan", R.drawable.catatan, Screen.Catatan.route),
            NavigationItem("Pengaturan", R.drawable.settings, Screen.Pengaturan.route)
        )

        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(32.dp),
                        tint = if (selected) {
                            if (item.label == "Beranda") Color.Unspecified else GreenMain
                        } else {
                            Color.Gray
                        }
                    )
                },
                label = { 
                    Text(
                        text = item.label,
                        color = if (selected) GreenMain else Color.Gray,
                        fontSize = 12.sp
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private data class NavigationItem(
    val label: String,
    val iconRes: Int,
    val route: String
)
