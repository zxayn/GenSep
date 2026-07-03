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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.generateresep.data.TokenManager
import com.example.generateresep.navigation.BottomNavigationBar
import com.example.generateresep.navigation.NavGraph
import com.example.generateresep.navigation.Screen
import com.example.generateresep.ui.theme.GenerateResepTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Cek token secara sinkron untuk menentukan start destination
        val startDestination = runBlocking {
            val token = tokenManager.authToken.first()
            if (token != null) Screen.Beranda.route else Screen.Login.route
        }

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
                        
                        val shouldShowBottomBar = currentRoute != Screen.Login.route && 
                                               currentRoute != Screen.Register.route &&
                                               currentRoute != null

                        Scaffold(
                            bottomBar = {
                                if (shouldShowBottomBar) {
                                    BottomNavigationBar(navController = navController)
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.background
                        ) { paddingValues ->
                            Box(modifier = Modifier.padding(paddingValues)) {
                                NavGraph(navController = navController, startDestination = startDestination)
                            }
                        }
                    }
                }
            }
        }
    }
}
