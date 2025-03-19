package com.example.farmmobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.di.appModule
import com.example.farmmobileapp.ui.screens.LoginScreen
import com.example.farmmobileapp.ui.screens.MainScreen
import com.example.farmmobileapp.ui.theme.AppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        enableEdgeToEdge()
        setContent {
            KoinContext {
                AppTheme {
                    AppNavigation()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Main : Screen("main")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) { // Start with LoginScreen
        composable(route = Screen.Login.route) {
            LoginScreen() // Show LoginScreen Composable
        }
        composable(route = Screen.Main.route) {
            MainScreen() // Show MainScreen Composable
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppTheme {
        AppNavigation() // Preview the navigation setup
    }
}