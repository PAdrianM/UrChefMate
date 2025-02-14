package com.adriang.urchefmate.recipe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adriang.urchefmate.recipe.data.Recipe
import com.adriang.urchefmate.recipe.data.RecipeCategory
import com.adriang.urchefmate.recipe.data.RecipeDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()

    // Obtener todas las recetas
    val recipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()


    // Actualizar el estado de favorito de una receta
    fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean) = viewModelScope.launch {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }
    // Obtener una receta por su ID
    fun getRecipeById(recipeId: Int): Flow<Recipe?> {
        return recipeDao.getRecipeById(recipeId)
    }

    // Insertar una nueva receta
    fun insertRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.insertRecipe(recipe)
    }

    // Obtener recetas favoritas
    val favoriteRecipes: Flow<List<Recipe>> = recipeDao.getFavoriteRecipes()

    // Obtener recetas por categoría
    fun getRecipesByCategory(category: RecipeCategory): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategory(category)
    }
}