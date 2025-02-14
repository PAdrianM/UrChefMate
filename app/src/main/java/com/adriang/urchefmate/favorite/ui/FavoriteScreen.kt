package com.adriang.urchefmate.favorite.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.adriang.urchefmate.R
import com.adriang.urchefmate.core.navigation.Routes
import com.adriang.urchefmate.recipe.data.Recipe
import com.adriang.urchefmate.recipe.data.RecipeCategory
import com.adriang.urchefmate.home.ui.BottomNavigationBar
import com.adriang.urchefmate.home.ui.EmptyState
import com.adriang.urchefmate.home.ui.SearchBarRecipeContainer
import com.adriang.urchefmate.recipe.viewmodel.RecipeViewModel
import com.adriang.urchefmate.ui.theme.DarkLetter
import com.adriang.urchefmate.ui.theme.White
import com.adriang.urchefmate.ui.theme.Yellow


@Composable
fun FavoriteScreen(
    navController: NavController,
    recipeViewModel: RecipeViewModel = viewModel()

) {
    // Observar las recetas favoritas del ViewModel
    val favoriteRecipes by recipeViewModel.favoriteRecipes.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        FavoriteContent(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .padding(16.dp),
            navController = navController,
            favoriteRecipes = favoriteRecipes
        )
    }
}

@Composable
fun FavoriteContent(
    modifier: Modifier,
    navController: NavController,
    favoriteRecipes: List<Recipe> // Recibir las recetas favoritas
) {
    var searchText by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var sortByTime by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        SearchBarRecipeContainer(
            searchText = searchText,
            onSearch = { query ->
                searchText = query
            },
            onFilterClick = { showFilterDialog = true }
        )

        Spacer(modifier = Modifier.padding(16.dp))


        FavoriteRecipeContainer(
            navController = navController,
            searchText = searchText,
            favoriteRecipes = favoriteRecipes,
            sortByTime = sortByTime
        )

        // Mostrar el diálogo de filtro
        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onApplyFilters = { newSortByTime ->
                    sortByTime = newSortByTime
                },
                initialSortByTime = sortByTime
            )
        }
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (Boolean) -> Unit,
    initialSortByTime: Boolean
) {
    // Estado para el filtro de tiempo
    var sortByTime by remember { mutableStateOf(initialSortByTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        title = { Text(text = stringResource(R.string.filter_recipe_dialog_title)) },
        text = {
            Column {
                // Opción para ordenar por tiempo de preparación
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { sortByTime = !sortByTime }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = sortByTime,
                        colors = CheckboxDefaults.colors(Yellow),
                        onCheckedChange = { sortByTime = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.order_by_time))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters(sortByTime)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Yellow)

            ) {
                Text(text = stringResource(R.string.accept_alert_dialog))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Yellow)
            ) {
                Text(text = stringResource(R.string.cancel_alert_dialog))
            }
        }
    )
}



@Composable
fun FavoriteRecipeContainer(
    navController: NavController,
    searchText: String,
    favoriteRecipes: List<Recipe>,
    sortByTime: Boolean
) {
    // Filtrar recetas según el texto de búsqueda
    val filteredRecipes = favoriteRecipes.filter { recipe ->
        recipe.title.contains(searchText, ignoreCase = true)
    }

    // Ordenar las recetas por tiempo de preparación si el filtro está activo
    val sortedRecipes = if (sortByTime) {
        filteredRecipes.sortedBy { it.cookingTime }
    } else {
        filteredRecipes
    }

    if (sortedRecipes.isEmpty()) {
        EmptyState {
            // Navegar a la pantalla de creación de recetas
            navController.navigate(Routes.CREATE_RECIPE)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sortedRecipes) { recipe ->
                FavoriteRecipeCard(recipe = recipe, navController = navController)
            }
        }
    }
}


@Composable
fun FavoriteRecipeCard(recipe: Recipe, navController: NavController) {
    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(150.dp) // Altura aumentada
            .clickable {
                // Navegar a la pantalla de detalles de la receta
                navController.navigate("recipe/${recipe.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Título de la receta
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = DarkLetter
                )
                // Tiempo de cocción
                Text(
                    text = stringResource(id = R.string.preparation_time, recipe.cookingTime.toInt()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkLetter
                )
            }
            // Imagen de la receta pegada al top, bottom y end de la card sin padding
            Image(
                painter = painterResource(id = recipe.imageResource),
                contentDescription = "Imagen de la receta",
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_6)
@Composable
fun FavoritePreview() {
    val navController = rememberNavController()

    val fakeRecipes = listOf(
        Recipe(
            id = 1,
            title = "Pizza Margarita con pina y comida deliciosa",
            description = "Clásica pizza italiana con tomate, mozzarella y albahaca.",
            cookingTime = 30.0,
            category = RecipeCategory.ItalianFood, // Ajusta según tu definición de RecipeCategory
            isFavorite = true,
            imageResource = R.drawable.fast_food
        ),
        Recipe(
            id = 2,
            title = "Sushi Roll",
            description = "Rollo de sushi con aguacate, salmón y alga nori.",
            cookingTime = 25.0,
            category = RecipeCategory.JapaneseFood,
            isFavorite = true,
            imageResource = R.drawable.fast_food
        ),
        Recipe(
            id = 3,
            title = "Tacos al Pastor",
            description = "Tortillas de maíz con carne de cerdo adobada y piña.",
            cookingTime = 20.0,
            category = RecipeCategory.MediterraneanFood,
            isFavorite = true,
            imageResource = R.drawable.fast_food
        )
    )

    FavoriteContent(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        favoriteRecipes = fakeRecipes
    )
}
