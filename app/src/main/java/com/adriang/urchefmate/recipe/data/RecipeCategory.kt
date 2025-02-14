package com.adriang.urchefmate.recipe.data

import com.adriang.urchefmate.R
import kotlinx.serialization.Serializable

@Serializable
enum class RecipeCategory(
    val displayName: String,
    val iconResId: Int,
    val defaultImageResId: Int
) {
    FastFood("Rápida", R.drawable.ic_fast_food, R.drawable.fast_food),
    ItalianFood("Italiana", R.drawable.ic_italian_food, R.drawable.italian_food),
    GuatemalanFood("Chapina", R.drawable.ic_guatemalan_food, R.drawable.guatemalan_food),
    ChineseFood("China", R.drawable.ic_chinese_food, R.drawable.chinese_food),
    JapaneseFood("Japonesa", R.drawable.ic_japan_food, R.drawable.japanese_food),
    MediterraneanFood("Mediterránea", R.drawable.ic_mediterranean_food, R.drawable.mediterranean_food),
    VeganFood("Vegana", R.drawable.ic_vegan_food, R.drawable.vegan_food),
    Deserts("Postres", R.drawable.ic_dessert_food, R.drawable.desserts),
    SeaFood("Mariscos", R.drawable.ic_seafood, R.drawable.seafood),
    HealthyFood("Saludable", R.drawable.ic_healthy_food, R.drawable.healthy_food)
}