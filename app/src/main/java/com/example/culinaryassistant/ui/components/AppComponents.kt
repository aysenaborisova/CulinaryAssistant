package com.example.culinaryassistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.culinaryassistant.model.Recipe
import com.example.culinaryassistant.model.Screen
import com.example.culinaryassistant.ui.theme.*

@Composable
fun ScreenWithNav(selected: Screen, onScreen: (Screen) -> Unit, content: @Composable (Modifier) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        bottomBar = { BottomNav(selected, onScreen) }
    ) { padding ->
        content(Modifier.padding(padding))
    }
}

@Composable
private fun BottomNav(selected: Screen, onScreen: (Screen) -> Unit) {
    val navItems = listOf(
        Triple(Screen.Catalog, "Главная", Icons.Outlined.Home),
        Triple(Screen.Favorites, "Избранное", Icons.Outlined.FavoriteBorder),
        Triple(Screen.Shopping, "Покупки", Icons.Outlined.ShoppingBag),
        Triple(Screen.Profile, "Профиль", Icons.Outlined.PersonOutline)
    )
    Row(
        Modifier.fillMaxWidth().background(AppCard).navigationBarsPadding().height(72.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { (screen, label, icon) ->
            val active = selected == screen || selected == Screen.AddRecipe && screen == Screen.Catalog
            Column(Modifier.clickable { onScreen(screen) }.padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, label, tint = if (active) AppOrange else Color(0xFFBDBDBD), modifier = Modifier.size(24.dp))
                Text(label, fontSize = 9.sp, color = if (active) AppOrange else Color(0xFFBDBDBD))
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, favorite: Boolean, onFavorite: () -> Unit, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(AppCard).clickable(onClick = onClick)) {
        Box {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.45f).clip(RoundedCornerShape(14.dp))
            )
            IconButton(onClick = onFavorite, modifier = Modifier.align(Alignment.TopEnd).size(36.dp)) {
                Icon(if (favorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (favorite) AppOrange else Color.White)
            }
        }
        Column(Modifier.padding(10.dp)) {
            Text(recipe.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(5.dp))
            Text("${recipe.cookingTime} мин · ${recipe.calories} ккал", fontSize = 10.sp, color = AppMuted)
        }
    }
}

@Composable
fun SearchField(value: String, onValue: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        placeholder = { Text("Найти рецепт", fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
        singleLine = true,
        shape = CircleShape
    )
}

@Composable
fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(CircleShape).background(if (selected) AppOrange else AppCard)
            .border(1.dp, if (selected) AppOrange else Color(0xFFE0E0E0), CircleShape)
            .clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp)
    ) { Text(text, color = if (selected) Color.White else AppInk, fontSize = 11.sp) }
}

@Composable
fun AppField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    type: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontSize = 12.sp) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = type),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun LabeledField(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    type: KeyboardType = KeyboardType.Text,
    multiline: Boolean = false
) {
    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
    AppField(value, onValue, placeholder, type, singleLine = !multiline)
}

@Composable
fun OrangeButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppOrange, disabledContainerColor = AppOrange.copy(alpha = .45f))
    ) { Text(text, color = Color.White) }
}

@Composable
fun RoundIcon(icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = modifier.size(44.dp).background(Color.White.copy(alpha = .92f), CircleShape)) {
        Icon(icon, null, tint = AppOrange)
    }
}

@Composable
fun Stat(text: String, color: Color) {
    Text(text, color = color, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
}
