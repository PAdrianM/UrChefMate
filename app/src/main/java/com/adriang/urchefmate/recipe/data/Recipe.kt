package com.adriang.urchefmate.recipe.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val cookingTime: Double,
    val category: RecipeCategory,
    val isFavorite: Boolean = false,
    val imageResource: Int
)