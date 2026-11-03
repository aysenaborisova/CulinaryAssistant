package com.example.culinaryassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.culinaryassistant.model.Recipe
import com.example.culinaryassistant.model.Screen
import com.example.culinaryassistant.ui.components.ScreenWithNav
import com.example.culinaryassistant.ui.theme.*

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    recipes: List<Recipe>,
    favorites: Int,
    shoppingCount: Int,
    onRecipe: (Recipe) -> Unit,
    onScreen: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    ScreenWithNav(Screen.Profile, onScreen) { navPadding ->
        Column(Modifier.fillMaxSize().then(navPadding).statusBarsPadding()) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).background(AppCard)
                    .padding(top = 26.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.size(98.dp).border(1.dp, AppOrange, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.PersonOutline, null, tint = AppOrange, modifier = Modifier.size(44.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(email, color = AppMuted, fontSize = 12.sp)
            }
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileStat("${recipes.size}", "Блюда", Modifier.weight(1f))
                ProfileStat("$favorites", "Избранное", Modifier.weight(1f), Color(0xFF62B46D))
                ProfileStat("$shoppingCount", "Покупки", Modifier.weight(1f))
            }
            Text("Мои блюда", Modifier.padding(horizontal = 20.dp), fontSize = 17.sp, fontWeight = FontWeight.Bold)
            if (recipes.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("Созданные вами блюда появятся здесь", color = AppMuted, fontSize = 13.sp)
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        MiniRecipe(recipe, Modifier.width(130.dp)) { onRecipe(recipe) }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onLogout, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Выйти из аккаунта", color = AppOrange)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, modifier: Modifier, color: Color = AppOrange) {
    Column(modifier.clip(RoundedCornerShape(12.dp)).background(AppCard).padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = AppMuted, fontSize = 10.sp)
    }
}

@Composable
private fun MiniRecipe(recipe: Recipe, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clip(RoundedCornerShape(10.dp)).background(AppCard).clickable(onClick = onClick)) {
        AsyncImage(recipe.imageUrl, recipe.title, Modifier.fillMaxWidth().height(72.dp), contentScale = ContentScale.Crop)
        Text(recipe.title, Modifier.padding(7.dp), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
