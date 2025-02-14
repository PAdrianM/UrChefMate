package com.adriang.urchefmate.recipe.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.adriang.urchefmate.R
import com.adriang.urchefmate.recipe.data.Recipe
import com.adriang.urchefmate.recipe.data.RecipeCategory
import com.adriang.urchefmate.recipe.viewmodel.RecipeViewModel
import com.adriang.urchefmate.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    navController: NavController,
    recipeViewModel: RecipeViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.top_bar_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Yellow
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        CreateRecipeContent(
            modifier = Modifier
                .background(White)
                .padding(innerPadding),
            onSaveRecipe = { newRecipe ->
                // Guardar la receta en la base de datos
                coroutineScope.launch {
                    recipeViewModel.insertRecipe(newRecipe)
                    navController.popBackStack()
                }
            }
        )
    }
}

@Composable
fun CreateRecipeContent(
    modifier: Modifier = Modifier,
    onSaveRecipe: (Recipe) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<RecipeCategory?>(null) }
    var cookingTime by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isFormValid = title.isNotEmpty() && selectedCategory != null && cookingTime.isNotEmpty() && description.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.recipe_title)) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = Yellow,
                unfocusedIndicatorColor = DarkGray
            )
        )

        // Texto "Categorías"
        Text(
            text = stringResource(id = R.string.select_category),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkLetter,
            modifier = Modifier.padding(top = 8.dp)
        )

        CategorieRecipe(selectedCategory) { selectedCategory = it }

        TextField(
            value = cookingTime,
            onValueChange = {
                if (it.matches(Regex("^\\d{0,5}(\\.\\d{0,1})?$"))) {
                    cookingTime = it
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.time_preparation)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = Yellow,
                unfocusedIndicatorColor = DarkGray
            )
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            label = { Text(text = stringResource(R.string.description_recipe)) },
            maxLines = 5,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = Yellow,
                unfocusedIndicatorColor = DarkGray
            )
        )

        Button(
            onClick = {
                val newRecipe = Recipe(
                    id = 0,
                    title = title,
                    cookingTime = cookingTime.toDouble(),
                    imageResource = selectedCategory!!.defaultImageResId,
                    description = description,
                    isFavorite = false,
                    category = selectedCategory!!
                )
                onSaveRecipe(newRecipe)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Yellow else LightGray,
                contentColor = DarkLetter
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = stringResource(id = R.string.save_recipe))
        }
    }
}


@Composable
fun CategorieRecipe(
    selectedCategory: RecipeCategory?,
    onCategorySelected: (RecipeCategory) -> Unit
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
                onClick = { onCategorySelected(category) }
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
            // Ícono de la categoría
            Image(
                painter = painterResource(id = category.iconResId),
                contentDescription = category.displayName,
                modifier = Modifier.size(30.dp) // Tamaño del ícono
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateRecipeScreenPreview() {
    val navController = rememberNavController()

    // Fake ViewModel sin base de datos ni AndroidViewModel
    val fakeViewModel = object {
        fun insertRecipe(recipe: Recipe) {
            println("Receta guardada: ${recipe.title}")
        }
    }

    CreateRecipeScreen(
        navController = navController,
        recipeViewModel = fakeViewModel as RecipeViewModel
    )
}