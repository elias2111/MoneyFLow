package me.elias.unabshop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : NavItem("home", Icons.Default.Home, "Home")
    object Stats : NavItem("stats", Icons.Default.BarChart, "Statistics")
    object Tx : NavItem("tx", Icons.Default.List, "Transactions")
    object Settings : NavItem("settings", Icons.Default.Settings, "Settings")
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
                        icon = { Icon(item.icon, null) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            when (screen) {
                NavItem.Home -> HomeScreen()
                NavItem.Stats -> StatisticsScreen()
                NavItem.Tx -> TransactionsScreen()
                NavItem.Settings -> SettingsScreen()
            }
        }
    }
}




