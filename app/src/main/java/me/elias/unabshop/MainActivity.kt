package me.elias.unabshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.elias.unabshop.ui.theme.UnabShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UnabShopTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onClickRegister = { nav.navigate("register") },
                onLoginSuccess = { nav.navigate("main") }
            )
        }

        composable("register") {
            RegisterScreen(
                onClickBack = { nav.popBackStack() },
                onRegisterSuccess = { nav.navigate("main") }
            )
        }

        composable("main") {
            NavigationApp()
        }
    }
}
