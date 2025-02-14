package com.adriang.urchefmate.home.data

data class FilterState(
    val showFavorites: Boolean = false, // Filtrar solo favoritos
    val sortByTime: Boolean = false // Ordenar por tiempo de preparación (menor a mayor)
)