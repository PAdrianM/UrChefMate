package com.adriang.urchefmate.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.adriang.urchefmate.core.navigation.Routes
import com.adriang.urchefmate.ui.theme.DarkLetter
import com.adriang.urchefmate.ui.theme.Yellow

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Obtener la entrada actual del back stack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definir los ítems del NavigationBar
    val items = listOf(
        BottomNavItem(
            route = Routes.FAVORITES, // Usar la ruta como String
            icon = Icons.Default.Favorite,
            label = ""
        ),
        BottomNavItem(
            route = Routes.HOME.replace("{user}", ""), // Usar la ruta como String
            icon = Icons.Default.Home,
            label = ""
        ),
        BottomNavItem(
            route = Routes.PROFILE, // Usar la ruta como String
            icon = Icons.Default.Person,
            label = ""
        )
    )

    // NavigationBar de Material 3 con color de fondo blanco
    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.route == Routes.HOME.replace("{user}", "")) { // Aplicar diseño personalizado SOLO al ítem de Home
                        Box(
                            modifier = Modifier
                                .padding(bottom = 32.dp) // Margen bottom para "sobresalir"
                                .size(48.dp) // Ajustar el tamaño del fondo circular
                                .background(color = Yellow, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color.White, // Icono de Home blanco
                                modifier = Modifier.align(Alignment.Center) // Centrar el icono dentro del Box
                            )
                        }
                    } else { // Íconos de Favorites y Profile sin fondo personalizado
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = DarkLetter
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        color = DarkLetter
                    )
                },
                selected = currentRoute == item.route,  // Comparar rutas como String
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to Home antes de navegar a la nueva ruta
                            popUpTo(Routes.HOME.replace("{user}", "{user}")) {
                                saveState = true // Guarda el estado de Home
                                inclusive = false // No incluye Home en el popUp, para que Home quede en el backstack
                            }
                            launchSingleTop = true // Evita múltiples instancias de la misma pantalla
                            restoreState = true // Restaura el estado de la nueva ruta si existe
                        }
                    }
                }
            )
        }
    }
}

// Data class para los ítems de navegación
data class BottomNavItem(
    val route: String, // Usar la ruta como String
    val icon: ImageVector,
    val label: String
)