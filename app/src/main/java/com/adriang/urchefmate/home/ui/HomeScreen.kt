package com.adriang.urchefmate.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adriang.urchefmate.R
import com.adriang.urchefmate.core.navigation.Routes
import com.adriang.urchefmate.home.data.FilterState
import com.adriang.urchefmate.login.data.SessionManager
import com.adriang.urchefmate.recipe.data.Recipe
import com.adriang.urchefmate.recipe.data.RecipeCategory
import com.adriang.urchefmate.recipe.viewmodel.RecipeViewModel
import com.adriang.urchefmate.ui.theme.DarkGray
import com.adriang.urchefmate.ui.theme.DarkLetter
import com.adriang.urchefmate.ui.theme.Gray
import com.adriang.urchefmate.ui.theme.LightGray
import com.adriang.urchefmate.ui.theme.SoftBeige
import com.adriang.urchefmate.ui.theme.White
import com.adriang.urchefmate.ui.theme.Yellow


@Composable
fun HomeScreen(
    user: String,
    navController: NavController,
    sessionManager: SessionManager,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    // Observar las recetas del ViewModel
    val recipes by recipeViewModel.recipes.collectAsState(initial = emptyList())

    // Estado del filtro
    var filterState by remember { mutableStateOf(FilterState()) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Estado de la búsqueda
    var searchText by remember { mutableStateOf("") }

    // Filtrar y ordenar las recetas
    val filteredRecipes = remember(recipes, filterState, searchText) {
        recipes
            .filter { recipe ->
                // Filtrar por búsqueda
                val matchesSearch = recipe.title.contains(searchText, ignoreCase = true)
                // Filtrar por favoritos
                val matchesFavorites = if (filterState.showFavorites) recipe.isFavorite else true
                matchesSearch && matchesFavorites
            }
            .sortedBy { recipe ->
                if (filterState.sortByTime) recipe.cookingTime else 0.0
            }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            // FloatingActionButton para agregar una nueva receta
            FloatingActionButton(
                onClick = {
                    // Navegar a la pantalla de creación de recetas
                    navController.navigate(Routes.CREATE_RECIPE)
                },
                modifier = Modifier
                    //.background(Yellow)
                    .size(height = 56.dp, width = 64.dp)
                    .padding(end = 8.dp, bottom = 2.dp),
                containerColor = Yellow,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar receta",
                    tint = White // Color del ícono
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            HomeContent(
                modifier = Modifier.align(Alignment.Center),
                user = user,
                navController = navController,
                recipes = filteredRecipes,
                searchText = searchText,
                onSearch = { query -> searchText = query },
                onFilterClick = { showFilterDialog = true },
                viewModel = recipeViewModel
            )

            // Mostrar el diálogo de filtros
            if (showFilterDialog) {
                FilterDialog(
                    onDismiss = { showFilterDialog = false },
                    onApplyFilters = { newFilterState ->
                        filterState = newFilterState
                    },
                    initialFilterState = filterState
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier,
    user: String,
    navController: NavController,
    recipes: List<Recipe>,
    searchText: String,
    onSearch: (String) -> Unit,
    onFilterClick: () -> Unit,
    viewModel: RecipeViewModel
) {
    var selectedCategory by remember { mutableStateOf<RecipeCategory?>(null) }

    Column(modifier = modifier) {
        TitleContainerUserName(user)
        Spacer(modifier = Modifier.padding(8.dp))

        // SearchBarRecipeContainer
        SearchBarRecipeContainer(
            searchText = searchText,
            onSearch = onSearch,
            onFilterClick = onFilterClick
        )

        Spacer(modifier = Modifier.padding(16.dp))
        CategorieRecipe(
            selectedCategory = selectedCategory,
            onCategorySelected = { category -> selectedCategory = category }
        )
        Spacer(modifier = Modifier.padding(16.dp))

        // RecipeContainer con el texto de búsqueda y la categoría seleccionada
        RecipeContainer(
            navController = navController,
            searchText = searchText,
            selectedCategory = selectedCategory,
            recipes = recipes,
            viewModel = viewModel
        )
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (FilterState) -> Unit,
    initialFilterState: FilterState
) {
    var showFavorites by remember { mutableStateOf(initialFilterState.showFavorites) }
    var sortByTime by remember { mutableStateOf(initialFilterState.sortByTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        title = { Text(text = stringResource(id = R.string.filter_recipe_dialog_title)) },
        text = {
            Column {
                // Opción para mostrar solo favoritos
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFavorites = !showFavorites }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = showFavorites,
                        colors = CheckboxDefaults.colors(Yellow),
                        onCheckedChange = { showFavorites = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.order_by_favorite),
                        color = DarkLetter)
                }

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
                    Text(text = stringResource(id = R.string.order_by_time),
                        color = DarkLetter)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters(FilterState(showFavorites, sortByTime))
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Yellow)

            ) {
                Text(text = stringResource(id = R.string.accept_alert_dialog))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Yellow)
            ) {
                Text(text = stringResource(id = R.string.cancel_alert_dialog))
            }
        }
    )
}


@Composable
fun RecipeContainer(
    navController: NavController,
    searchText: String,
    selectedCategory: RecipeCategory?,
    recipes: List<Recipe>,
    viewModel: RecipeViewModel // Añadir el ViewModel como parámetro
) {
    // Filtrar recetas según el texto de búsqueda y la categoría seleccionada
    val filteredRecipes = recipes.filter { recipe ->
        recipe.title.contains(searchText, ignoreCase = true) &&
                (selectedCategory == null || recipe.category == selectedCategory)
    }

    // Si no hay recetas, mostramos el EmptyState
    if (filteredRecipes.isEmpty()) {
        EmptyState(onAddClick = {
            // Navegar a la pantalla de creación de recetas
            navController.navigate(Routes.CREATE_RECIPE)

        })
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            items(filteredRecipes) { recipe ->
                RecipeCard(recipe, navController, viewModel)
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, navController: NavController, viewModel: RecipeViewModel) {
    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .width(272.dp)
            .background(color = DarkGray, shape = RoundedCornerShape(28.dp))
            .clickable {
                // Navegar a la pantalla de detalles de la receta
                navController.navigate("recipe/${recipe.id}")
            }
    ) {
        Card(
            modifier = Modifier
                .height(470.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {

                Image(
                    painter = painterResource(id = recipe.imageResource),
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Card que contiene el ícono de favorito
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(40.dp),
                    shape = RoundedCornerShape(30),
                    colors = CardDefaults.cardColors(
                        containerColor = if (recipe.isFavorite) Color.White else Color.White
                    )
                ) {
                    IconButton(
                        onClick = {
                            // Actualizar el estado de favorito en la base de datos
                            viewModel.updateFavoriteStatus(recipe.id, !recipe.isFavorite)
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (recipe.isFavorite) Yellow else Gray
                        )
                    }
                }


                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .background(color = White, shape = RoundedCornerShape(28.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = DarkLetter
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.preparation_time, recipe.cookingTime.toInt()),
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_state),
                contentDescription = "Imagen de estado vacío",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.empty_state),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategorieRecipe(
    selectedCategory: RecipeCategory?,
    onCategorySelected: (RecipeCategory?) -> Unit
) {
    val categories = remember {
        RecipeCategory.values().toList()
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                isSelected = category == selectedCategory,
                onClick = {
                    // Alternar la selección de la categoría
                    onCategorySelected(if (category == selectedCategory) null else category)
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: RecipeCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 54.dp)
            .background(
                color = if (isSelected) Yellow else SoftBeige,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = category.iconResId),
                contentDescription = category.displayName,
                modifier = Modifier.size(30.dp)
            )
            // Nombre de la categoría
            Text(
                text = category.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) White else DarkLetter
            )
        }
    }
}

@Composable
fun SearchBarRecipeContainer(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearch: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // SearchBar
        Box(
            modifier = Modifier
                .weight(1f)
                .background(LightGray, RoundedCornerShape(16.dp))
                .height(56.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de lupa
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(LightGray, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DarkGray
                    )
                }

                TextField(
                    value = searchText,
                    onValueChange = { onSearch(it) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .fillMaxHeight(),
                    placeholder = {
                        Text(text = stringResource(id = R.string.find_ur_recipes), color = Color.Gray)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = DarkLetter,
                        unfocusedTextColor = DarkLetter,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    maxLines = 1
                )
            }
        }

        // Botón de filtros
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(56.dp)
                .background(Yellow, RoundedCornerShape(12.dp)),
        ) {
            Icon(
                modifier = modifier.size(22.dp),
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "Filters",
                tint = DarkLetter
            )
        }
    }
}

@Composable
fun TitleContainerUserName(user: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),

        ) {
        UserName(user)
        Spacer(modifier = Modifier.padding(4.dp))
        TitleHome()
    }
}

@Composable
fun TitleHome() {
    Text(
        text = stringResource(id = R.string.title_home),
        style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkLetter
        )
    )
}

@Composable
fun UserName(user: String) {
    Text(
        text = "Hola $user!",
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGray
        )
    )
}

//@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7A)
//@Composable
//fun HomePreview() {
//    val navController = rememberNavController()
//
//    // Lista de recetas de ejemplo
//    val sampleRecipes = listOf(
//        Recipe(
//            id = 1,
//            title = "Paella de Mariscos",
//            cookingTime = 45.0,
//            imageResource = R.drawable.fast_food,
//            description = "Deliciosa paella con mariscos frescos.",
//            isFavorite = true,
//            category = RecipeCategory.SeaFood
//        ),
//        Recipe(
//            id = 2,
//            title = "Pasta Carbonara",
//            cookingTime = 30.0,
//            imageResource = R.drawable.fast_food,
//            description = "Clásica pasta italiana con huevo, queso y panceta.",
//            isFavorite = false,
//            category = RecipeCategory.ItalianFood
//        ),
//        Recipe(
//            id = 3,
//            title = "Tacos Rápidos",
//            cookingTime = 25.0,
//            imageResource = R.drawable.fast_food,
//            description = "Tacos rápidos con carne asada y salsa picante.",
//            isFavorite = true,
//            category = RecipeCategory.FastFood
//        )
//    )
//
//    // Simular el ViewModel con recetas de ejemplo
//    val recipeViewModel = viewModel<RecipeViewModel>()
//    recipeViewModel.insertRecipe(sampleRecipes[0])
//    recipeViewModel.insertRecipe(sampleRecipes[1])
//    recipeViewModel.insertRecipe(sampleRecipes[2])
//
//    HomeScreen(
//        user = "Adrian",
//        navController = navController,
//        recipeViewModel = recipeViewModel
//    )
//}