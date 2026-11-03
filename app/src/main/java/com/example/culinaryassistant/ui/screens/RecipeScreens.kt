package com.example.culinaryassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.culinaryassistant.model.*
import com.example.culinaryassistant.ui.components.*
import com.example.culinaryassistant.ui.theme.*

@Composable
fun DetailScreen(
    recipe: Recipe,
    favorite: Boolean,
    onBack: () -> Unit,
    onFavorite: () -> Unit,
    onShopping: () -> Unit,
    onCook: () -> Unit
) {
    LazyColumn(Modifier.fillMaxSize().background(AppBackground)) {
        item {
            Box {
                AsyncImage(recipe.imageUrl, recipe.title, Modifier.fillMaxWidth().height(300.dp), contentScale = ContentScale.Crop)
                RoundIcon(Icons.AutoMirrored.Filled.ArrowBack, Modifier.statusBarsPadding().padding(start = 16.dp, top = 12.dp).align(Alignment.TopStart), onBack)
                RoundIcon(if (favorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, Modifier.statusBarsPadding().padding(end = 16.dp, top = 12.dp).align(Alignment.TopEnd), onFavorite)
            }
        }
        item {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Text(recipe.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(recipe.description, color = AppMuted, fontSize = 14.sp, lineHeight = 20.sp)
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Stat("${recipe.cookingTime} мин", AppOrange)
                    Stat("${recipe.calories} ккал", Color(0xFF61B76B))
                    Stat("${recipe.servings} порц.", AppOrange)
                }
                Spacer(Modifier.height(22.dp))
                Text("Ингредиенты", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                recipe.ingredients.forEach { ingredient ->
                    Text(
                        ingredient,
                        Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(8.dp)).background(AppCard).padding(14.dp),
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(22.dp))
                Text("Приготовление", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                recipe.steps.forEachIndexed { index, step ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(10.dp))
                            .background(AppCard).padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            Modifier.size(32.dp).background(Color(0xFFFFF0E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", color = AppOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(step, Modifier.weight(1f), fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                OrangeButton("В список покупок", onClick = onShopping)
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onCook,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppOrange)
                ) { Text("Начать готовить") }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CookingScreen(recipe: Recipe, onBack: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().background(AppBackground)) {
        Box {
            AsyncImage(recipe.imageUrl, recipe.title, Modifier.fillMaxWidth().height(250.dp), contentScale = ContentScale.Crop)
            RoundIcon(Icons.AutoMirrored.Filled.ArrowBack, Modifier.statusBarsPadding().padding(start = 16.dp, top = 12.dp), onBack)
        }
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text(recipe.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Шаг ${page + 1} из ${recipe.steps.size}", color = AppMuted, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(Color(0xFFE1E1E1))) {
                Box(Modifier.fillMaxWidth((page + 1f) / recipe.steps.size).fillMaxHeight().background(AppOrange))
            }
            Spacer(Modifier.height(24.dp))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(AppCard).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Row {
                    Box(Modifier.size(42.dp).background(Color(0xFFFFF0E9), CircleShape), contentAlignment = Alignment.Center) {
                        Text("${page + 1}", color = AppOrange, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(recipe.steps[page], Modifier.weight(1f), fontSize = 16.sp, lineHeight = 23.sp)
                }
            }
            Spacer(Modifier.weight(1f))
            if (page < recipe.steps.lastIndex) OrangeButton("Далее") { page++ }
            else OrangeButton("Готово", onClick = onBack)
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { if (page > 0) page-- else onBack() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppOrange)
            ) { Text("Назад") }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
fun AddRecipeScreen(
    onBack: () -> Unit,
    onSave: (NewRecipe, (Boolean) -> Unit) -> Unit,
    onScreen: (Screen) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Завтрак") }
    var imageUrl by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    ScreenWithNav(Screen.AddRecipe, onScreen) { navPadding ->
        Column(
            Modifier.fillMaxSize().then(navPadding).statusBarsPadding().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundIcon(Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
                Spacer(Modifier.width(10.dp))
                Text("Новый рецепт", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            LabeledField("Название", title, { title = it }, "Например, Тирамису")
            LabeledField("Описание", description, { description = it }, "Коротко о блюде")
            Text("Категория", fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Завтрак", "Обед", "Ужин").forEach { value ->
                    CategoryChip(value, value == category) { category = value }
                }
            }
            LabeledField("Время готовки", time, { time = it }, "Например, 40 мин", KeyboardType.Number)
            LabeledField("Калорийность", calories, { calories = it }, "Например, 350 ккал", KeyboardType.Number)
            LabeledField("Количество порций", servings, { servings = it }, "Например, 4", KeyboardType.Number)
            LabeledField("Ссылка на изображение", imageUrl, { imageUrl = it }, "https://...", KeyboardType.Uri)
            LabeledField("Ингредиенты", ingredients, { ingredients = it }, "Каждый ингредиент с новой строки", multiline = true)
            LabeledField("Шаги приготовления", steps, { steps = it }, "Каждый шаг с новой строки", multiline = true)
            Spacer(Modifier.height(8.dp))
            OrangeButton(
                if (busy) "Сохраняем..." else "Сохранить рецепт",
                !busy && title.isNotBlank() && description.isNotBlank() && time.toIntOrNull()?.let { it > 0 } == true &&
                    calories.toIntOrNull()?.let { it > 0 } == true && servings.toIntOrNull()?.let { it > 0 } == true &&
                    (imageUrl.startsWith("https://") || imageUrl.startsWith("http://")) &&
                    ingredients.isNotBlank() && steps.isNotBlank()
            ) {
                busy = true
                error = false
                onSave(
                    NewRecipe(
                        title = title.trim(),
                        description = description.trim(),
                        cookingTime = time.toInt(),
                        calories = calories.toInt(),
                        servings = servings.toInt(),
                        category = category,
                        imageUrl = imageUrl.trim(),
                        ingredients = ingredients.lines().map { it.trim() }.filter { it.isNotBlank() },
                        steps = steps.lines().map { it.trim() }.filter { it.isNotBlank() }
                    )
                ) { ok -> busy = false; error = !ok }
            }
            if (error) {
                Text("Не удалось сохранить рецепт. Проверьте подключение и повторите.", color = AppOrange, fontSize = 12.sp)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
