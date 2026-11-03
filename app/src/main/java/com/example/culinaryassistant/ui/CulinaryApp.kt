package com.example.culinaryassistant.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.culinaryassistant.data.SupabaseApi
import com.example.culinaryassistant.model.Recipe
import com.example.culinaryassistant.model.Screen
import com.example.culinaryassistant.model.SessionUser
import com.example.culinaryassistant.ui.screens.*
import com.example.culinaryassistant.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CulinaryApp() {
    var screen by remember { mutableStateOf(Screen.Login) }
    var selected by remember { mutableStateOf<Recipe?>(null) }
    var recipes by remember { mutableStateOf(emptyList<Recipe>()) }
    var loading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var currentUser by remember { mutableStateOf<SessionUser?>(null) }
    val favorites = remember { mutableStateListOf<String>() }
    val shopping = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()

    BackHandler(enabled = screen != Screen.Login && screen != Screen.Catalog) {
        screen = when (screen) {
            Screen.Register -> Screen.Login
            Screen.Cooking -> Screen.Detail
            Screen.Detail, Screen.AddRecipe, Screen.Favorites, Screen.Shopping, Screen.Profile -> Screen.Catalog
            Screen.Login, Screen.Catalog -> screen
        }
    }

    fun loadRecipes() {
        val accessToken = currentUser?.accessToken ?: return
        scope.launch {
            loading = true
            loadError = null
            runCatching { SupabaseApi.getRecipes(accessToken) }
                .onSuccess { recipes = it }
                .onFailure { loadError = "Не удалось загрузить рецепты" }
            loading = false
        }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AppOrange,
            background = AppBackground,
            surface = AppCard,
            onBackground = AppInk
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = AppBackground) {
            Crossfade(screen, label = "screen") { current ->
                when (current) {
                    Screen.Login -> LoginScreen(
                        onRegister = { screen = Screen.Register },
                        onLogin = { email, password, done ->
                            scope.launch {
                                val result = SupabaseApi.signIn(email, password)
                                val user = result.user
                                if (user != null) {
                                    currentUser = user
                                    loadRecipes()
                                    screen = Screen.Catalog
                                }
                                done(result)
                            }
                        }
                    )
                    Screen.Register -> RegisterScreen(
                        onBack = { screen = Screen.Login },
                        onRegister = { name, email, password, done ->
                            scope.launch {
                                val result = SupabaseApi.signUp(name, email, password)
                                val user = result.user
                                if (user != null) {
                                    currentUser = user
                                    loadRecipes()
                                    screen = Screen.Catalog
                                }
                                done(result)
                            }
                        }
                    )
                    Screen.Catalog -> CatalogScreen(
                        recipes,
                        loading,
                        loadError,
                        favorites,
                        { selected = it; screen = Screen.Detail },
                        { id -> if (id in favorites) favorites.remove(id) else favorites.add(id) },
                        { screen = it },
                        { screen = Screen.AddRecipe },
                        ::loadRecipes
                    )
                    Screen.Detail -> selected?.let { recipe ->
                        DetailScreen(
                            recipe,
                            recipe.id in favorites,
                            { screen = Screen.Catalog },
                            { if (recipe.id in favorites) favorites.remove(recipe.id) else favorites.add(recipe.id) },
                            {
                                recipe.ingredients.forEach { ingredient ->
                                    if (ingredient !in shopping) shopping.add(ingredient)
                                }
                                screen = Screen.Shopping
                            },
                            { screen = Screen.Cooking }
                        )
                    }
                    Screen.Cooking -> selected?.let { CookingScreen(it) { screen = Screen.Detail } }
                    Screen.Favorites -> FavoritesScreen(
                        recipes.filter { it.id in favorites },
                        { selected = it; screen = Screen.Detail },
                        { id -> favorites.remove(id) },
                        { screen = it }
                    )
                    Screen.Shopping -> ShoppingScreen(shopping) { screen = it }
                    Screen.AddRecipe -> AddRecipeScreen(
                        { screen = Screen.Catalog },
                        { recipe, done ->
                            scope.launch {
                                val user = currentUser
                                val ok = user != null && SupabaseApi.addRecipe(recipe, user)
                                if (ok) {
                                    recipes = runCatching { SupabaseApi.getRecipes(user.accessToken) }.getOrDefault(recipes)
                                    screen = Screen.Profile
                                }
                                done(ok)
                            }
                        },
                        { screen = it }
                    )
                    Screen.Profile -> currentUser?.let { user ->
                        ProfileScreen(
                            user.name,
                            user.email,
                            recipes.filter { it.userId == user.id },
                            favorites.size,
                            shopping.size,
                            { selected = it; screen = Screen.Detail },
                            { screen = it },
                            {
                                currentUser = null
                                recipes = emptyList()
                                favorites.clear()
                                shopping.clear()
                                screen = Screen.Login
                            }
                        )
                    }
                }
            }
        }
    }
}
