package com.example.culinaryassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinaryassistant.model.Recipe
import com.example.culinaryassistant.model.Screen
import com.example.culinaryassistant.ui.components.*
import com.example.culinaryassistant.ui.theme.*

@Composable
fun CatalogScreen(
    recipes: List<Recipe>,
    loading: Boolean,
    error: String?,
    favorites: List<String>,
    onRecipe: (Recipe) -> Unit,
    onFavorite: (String) -> Unit,
    onScreen: (Screen) -> Unit,
    onAdd: () -> Unit,
    onRetry: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Все") }
    val filtered = recipes.filter {
        it.title.contains(query, true) && (category == "Все" || it.category == category || it.category.isBlank())
    }
    ScreenWithNav(Screen.Catalog, onScreen) { navPadding ->
        Column(Modifier.fillMaxSize().then(navPadding).statusBarsPadding().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Что приготовим?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppInk)
                Text("＋", color = AppOrange, fontSize = 28.sp, modifier = Modifier.clickable(onClick = onAdd))
            }
            Spacer(Modifier.height(18.dp))
            SearchField(query) { query = it }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Все", "Завтрак", "Обед", "Ужин").forEach { CategoryChip(it, it == category) { category = it } }
            }
            Spacer(Modifier.height(18.dp))
            Text("Популярное", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AppOrange) }
            } else if (error != null) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(error, color = AppMuted, fontSize = 13.sp)
                    TextButton(onClick = onRetry) { Text("Повторить", color = AppOrange) }
                }
            } else if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Рецепты не найдены", color = AppMuted, fontSize = 13.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 18.dp)
                ) {
                    items(filtered, key = { it.id }) { recipe ->
                        RecipeCard(recipe, recipe.id in favorites, { onFavorite(recipe.id) }) { onRecipe(recipe) }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    recipes: List<Recipe>,
    onRecipe: (Recipe) -> Unit,
    onFavorite: (String) -> Unit,
    onScreen: (Screen) -> Unit
) {
    ScreenWithNav(Screen.Favorites, onScreen) { navPadding ->
        Column(Modifier.fillMaxSize().then(navPadding).statusBarsPadding().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(24.dp))
            Text("Избранное", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("${recipes.size} рецепта", color = AppMuted, fontSize = 12.sp)
            Spacer(Modifier.height(18.dp))
            if (recipes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Здесь появятся блюда, которые вы отметите сердечком", color = AppMuted, fontSize = 13.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(recipe, true, { onFavorite(recipe.id) }, { onRecipe(recipe) })
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingScreen(products: MutableList<String>, onScreen: (Screen) -> Unit) {
    val checked = remember { mutableStateListOf<String>() }
    ScreenWithNav(Screen.Shopping, onScreen) { navPadding ->
        Column(Modifier.fillMaxSize().then(navPadding).statusBarsPadding().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(24.dp))
            Text("Список покупок", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("${products.size} продуктов", color = AppMuted, fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
            if (products.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Добавьте ингредиенты из карточки блюда", color = AppMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    items(products) { product ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(9.dp)).background(AppCard)
                                .clickable { if (product in checked) checked.remove(product) else checked.add(product) }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(if (product in checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank, null, tint = AppOrange, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(product, fontSize = 13.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = { products.clear(); checked.clear() },
                enabled = products.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppOrange)
            ) { Text("Очистить список") }
        }
    }
}
