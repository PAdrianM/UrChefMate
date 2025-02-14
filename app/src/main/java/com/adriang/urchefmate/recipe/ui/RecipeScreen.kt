package com.adriang.urchefmate.recipe.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.adriang.urchefmate.R
import com.adriang.urchefmate.recipe.data.Recipe
import com.adriang.urchefmate.recipe.data.RecipeCategory
import com.adriang.urchefmate.recipe.viewmodel.RecipeViewModel
import com.adriang.urchefmate.ui.theme.DarkGray
import com.adriang.urchefmate.ui.theme.DarkLetter
import com.adriang.urchefmate.ui.theme.LightGray
import com.adriang.urchefmate.ui.theme.White
import com.adriang.urchefmate.ui.theme.Yellow

@Composable
fun RecipeScreen(
    recipeId: Int,
    navController: NavController,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    // Observar la receta desde el ViewModel
    val recipe by recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            RecipeTopBar(
                (recipe?.title
                    ?: Text(text = stringResource(id = R.string.title_recipe))).toString(),
                navController
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
        ) {
            if (recipe != null) {
                RecipeContent(recipe = recipe!!, recipeViewModel = recipeViewModel)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Imagen del Empty State
                    Image(
                        painter = painterResource(id = R.drawable.ic_empty_state_recipe),
                        contentDescription = "Cargando receta",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.message_charging_recipe),
                        color = DarkLetter,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Composable
fun RecipeContent(recipe: Recipe, recipeViewModel: RecipeViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {

        ImageRecipe(recipe)

        //Detalles de receta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            RecipeDetailsCard(recipe, recipeViewModel)
        }

        // FAB para marcar/desmarcar como favorito
        FloatingActionButton(
            onClick = {
                recipeViewModel.updateFavoriteStatus(recipe.id, !recipe.isFavorite)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = if (recipe.isFavorite) Yellow else LightGray,
            contentColor = DarkLetter
        ) {
            Icon(
                imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (recipe.isFavorite) "Quitar de favoritos" else "Marcar como favorito"
            )
        }
    }
}


@Composable
fun ImageRecipe(recipe: Recipe) {
    Image(
        painter = painterResource(id = recipe.imageResource),
        contentDescription = recipe.title,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RecipeDetailsCard(recipe: Recipe, recipeViewModel: RecipeViewModel) {
    Card(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.65f),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(28.dp)
        ) {
            Text(
                text = recipe.title,
                fontSize = 32.sp,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = DarkLetter,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Categoría y tiempo de cocción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Categoría
                Text(
                    text = stringResource(id = R.string.title_category_recipe),
                    color = DarkLetter
                )
                CategoryCard(recipe.category)
            }

            // Tiempo de cocción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.title_time_recipe),
                    color = DarkLetter
                )
                CookingTimeCard(recipe.cookingTime.toString())
            }

            // Título "Descripción"
            Text(
                text = stringResource(id = R.string.description_title),
                style = MaterialTheme.typography.headlineSmall,
                color = DarkLetter,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Descripción de la receta (desplazable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = recipe.description,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray
                )
            }
        }
    }
}


@Composable
fun CookingTimeCard(cookingTime: String) {

    val cookingTimeInt = try {
        cookingTime.toDouble().toInt()
    } catch (e: NumberFormatException) { 0 }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Yellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = "$cookingTimeInt mins",
            modifier = Modifier.padding(8.dp),
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = DarkLetter
        )
    }
}


@Composable
fun CategoryCard(category: RecipeCategory) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Yellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ícono de la categoría
            Image(
                painter = painterResource(id = category.iconResId),
                contentDescription = category.displayName,
                modifier = Modifier.size(30.dp)
            )
            // Nombre de la categoría
            Text(
                text = category.displayName,
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = DarkLetter
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeTopBar(title: String, navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Volver")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Yellow
        )
    )
}

//@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
//@Composable
//fun RecipeScreenPreview() {
//    // Simular una receta para el Preview
//    val sampleRecipe = Recipe(
//        id = 1, // ID de la receta
//        title = "Paella de Mariscos",
//        cookingTime = 45.0, // Tiempo de cocción como Double
//        imageResource = R.drawable.fast_food, // Imagen de la receta (asegúrate de tener este recurso)
//        description = "Deliciosa paella con mariscos frescos. " +
//                "Esta receta tradicional española combina arroz, mariscos, " +
//                "y especias para crear un plato lleno de sabor y aroma. " +
//                "Perfecta para compartir en familia o con amigos.",
//        isFavorite = true,
//        category = RecipeCategory.SeaFood // Categoría de la receta
//    )
//}

