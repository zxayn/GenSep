package com.example.generateresep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.generateresep.navigation.BottomNavigationBar
import com.example.generateresep.navigation.NavGraph
import com.example.generateresep.navigation.Screen
import com.example.generateresep.ui.theme.GenerateResepTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            GenerateResepTheme {
                var showSplashScreen by remember { mutableStateOf(true) }
                val navController = rememberNavController()
                
                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplashScreen = false
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplashScreen) {
                        SplashScreen()
                    } else {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        
                        val showBottomBar = currentRoute !in listOf(
                            Screen.Login.route,
                            Screen.Register.route
                        )

                        Scaffold(
                            bottomBar = {
                                if (showBottomBar) {
                                    BottomNavigationBar(navController = navController)
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.background
                        ) { paddingValues ->
                            Box(modifier = Modifier.padding(paddingValues)) {
                                NavGraph(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
