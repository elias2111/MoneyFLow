package me.elias.unabshop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

sealed class NavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : NavItem("home", Icons.Filled.Home, "Home")
    object Stats : NavItem("stats", Icons.Filled.BarChart, "Statistics")
    object Tx : NavItem("tx", Icons.Filled.List, "Transactions")
    object Settings : NavItem("settings", Icons.Filled.Settings, "Settings")
}

@Composable
fun NavigationApp() {

    var screen by remember { mutableStateOf<NavItem>(NavItem.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    NavItem.Home,
                    NavItem.Stats,
                    NavItem.Tx,
                    NavItem.Settings
                ).forEach { item ->
                    NavigationBarItem(
                        selected = screen.route == item.route,
                        onClick = { screen = item },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->

        // 👇 IMPORTANTE: aplicar padding a las pantallas
        val modifier = Modifier.padding(padding)

        when (screen) {
            NavItem.Home -> HomeScreen()
            NavItem.Stats -> StatisticsScreen()
            NavItem.Tx -> TransactionsScreen()
            NavItem.Settings -> SettingsScreen()
        }
    }
}


