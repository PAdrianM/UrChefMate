package com.adriang.urchefmate.core.navigation

import kotlinx.serialization.Serializable

object Routes {
    const val LOGIN = "login"
    const val HOME = "home/{user}"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val RECIPE = "recipe/{recipeId}"
    const val CREATE_RECIPE = "create_recipe"
}