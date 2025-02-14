package com.adriang.urchefmate.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adriang.urchefmate.favorite.ui.FavoriteScreen
import com.adriang.urchefmate.home.ui.HomeScreen
import com.adriang.urchefmate.login.ui.LoginScreen
import com.adriang.urchefmate.login.data.SessionManager
import com.adriang.urchefmate.profile.ui.ProfileScreen
import com.adriang.urchefmate.recipe.ui.CreateRecipeScreen
import com.adriang.urchefmate.recipe.ui.RecipeScreen


@Composable
fun NavigationWrapper(sessionManager: SessionManager) {
    val navController = rememberNavController()

    // Verificar si el usuario ya está logueado
    val isLoggedIn = sessionManager.isLoggedIn()

    // Navegar a la pantalla correcta según el estado de autenticación
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val userEmail = sessionManager.getUser() ?: ""
            navController.navigate("home/$userEmail") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true } // Borra TODO antes de ir a Home
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                navigateToHome = { user ->
                    val password = sessionManager.getUserPassword() ?: "" // Obtener la contraseña
                    sessionManager.saveUser(user, password) // Guardar el correo y la contraseña
                    navController.navigate("home/$user") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                sessionManager = sessionManager
            )
        }

        composable(
            route = Routes.HOME,
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) { backStackEntry ->
            val user = backStackEntry.arguments?.getString("user") ?: ""
            HomeScreen(user = user, navController = navController, sessionManager = sessionManager)
        }

        composable(Routes.FAVORITES) {
            FavoriteScreen(navController = navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController, sessionManager = sessionManager)
        }

        composable(
            route = Routes.RECIPE,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            RecipeScreen(recipeId = recipeId, navController = navController)
        }

        composable(Routes.CREATE_RECIPE) {
            CreateRecipeScreen(navController = navController)
        }
    }
}
