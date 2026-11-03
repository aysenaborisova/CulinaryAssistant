package com.example.culinaryassistant.model

data class Recipe(
    val id: String,
    val userId: String?,
    val title: String,
    val description: String,
    val cookingTime: Int,
    val calories: Int,
    val servings: Int,
    val imageUrl: String,
    val category: String,
    val ingredients: List<String>,
    val steps: List<String>
)

data class NewRecipe(
    val title: String,
    val description: String,
    val cookingTime: Int,
    val calories: Int,
    val servings: Int,
    val category: String,
    val imageUrl: String,
    val ingredients: List<String>,
    val steps: List<String>
)

data class SessionUser(
    val id: String,
    val name: String,
    val email: String,
    val accessToken: String
)

data class AuthResult(
    val user: SessionUser? = null,
    val message: String? = null,
    val registrationPending: Boolean = false
)

enum class Screen {
    Login,
    Register,
    Catalog,
    Detail,
    Cooking,
    Favorites,
    Shopping,
    AddRecipe,
    Profile
}
