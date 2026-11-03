package com.example.culinaryassistant.data

import com.example.culinaryassistant.BuildConfig
import com.example.culinaryassistant.model.Recipe
import com.example.culinaryassistant.model.NewRecipe
import com.example.culinaryassistant.model.AuthResult
import com.example.culinaryassistant.model.SessionUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object SupabaseApi {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun getRecipes(accessToken: String): List<Recipe> = withContext(Dispatchers.IO) {
        val array = JSONArray(
            request(
                "${BuildConfig.SUPABASE_URL}/rest/v1/recipes?select=*&order=title.asc",
                accessToken = accessToken
            )
        )
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            Recipe(
                id = item.getString("id"),
                userId = item.optString("user_id").takeIf { it.isNotBlank() && it != "null" },
                title = item.getString("title"),
                description = item.getString("description"),
                cookingTime = item.getInt("cooking_time"),
                calories = item.getInt("calories"),
                servings = item.getInt("servings"),
                imageUrl = item.getString("image_url"),
                category = item.getString("category"),
                ingredients = item.getJSONArray("ingredients").toStrings(),
                steps = item.getJSONArray("steps").toStrings()
            )
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val body = JSONObject().put("email", email).put("password", password).toString()
        runCatching {
            AuthResult(user = parseUser(request("${BuildConfig.SUPABASE_URL}/auth/v1/token?grant_type=password", "POST", body)))
        }.getOrElse { error ->
            AuthResult(message = authMessage(error.message, isLogin = true))
        }
    }

    suspend fun signUp(name: String, email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("email", email)
            .put("password", password)
            .put("data", JSONObject().put("name", name))
            .toString()
        runCatching {
            val response = JSONObject(request("${BuildConfig.SUPABASE_URL}/auth/v1/signup", "POST", body))
            val accessToken = response.optString("access_token")
            if (accessToken.isNotBlank()) {
                AuthResult(user = parseUser(response.toString()))
            } else {
                val identities = response.optJSONObject("user")?.optJSONArray("identities")
                if (identities != null && identities.length() == 0) {
                    AuthResult(message = "Аккаунт с таким email уже существует. Войдите со старым паролем или восстановите его.")
                } else {
                    AuthResult(
                        message = "Аккаунт создан. Подтвердите email по ссылке в письме, затем войдите.",
                        registrationPending = true
                    )
                }
            }
        }.getOrElse { error ->
            AuthResult(message = authMessage(error.message, isLogin = false))
        }
    }

    suspend fun addRecipe(recipe: NewRecipe, user: SessionUser): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("user_id", user.id)
            .put("title", recipe.title)
            .put("description", recipe.description)
            .put("cooking_time", recipe.cookingTime)
            .put("calories", recipe.calories)
            .put("servings", recipe.servings)
            .put("category", recipe.category)
            .put("image_url", recipe.imageUrl)
            .put("ingredients", JSONArray(recipe.ingredients))
            .put("steps", JSONArray(recipe.steps))
            .toString()
        runCatching {
            request(
                "${BuildConfig.SUPABASE_URL}/rest/v1/recipes",
                method = "POST",
                body = body,
                minimal = true,
                accessToken = user.accessToken
            )
            true
        }.getOrDefault(false)
    }

    private fun parseUser(response: String): SessionUser {
        val root = JSONObject(response)
        val user = root.getJSONObject("user")
        val email = user.getString("email")
        val name = user.optJSONObject("user_metadata")?.optString("name").orEmpty().ifBlank { email.substringBefore("@") }
        return SessionUser(user.getString("id"), name, email, root.getString("access_token"))
    }

    private fun authMessage(message: String?, isLogin: Boolean): String {
        val source = message.orEmpty().lowercase()
        return when {
            "invalid login credentials" in source -> "Неверный email или пароль. Если вы уже регистрировались, используйте первоначальный пароль."
            "email not confirmed" in source -> "Email ещё не подтверждён. Откройте письмо от Supabase и перейдите по ссылке."
            "user already registered" in source || "already been registered" in source -> "Аккаунт с таким email уже существует."
            "password" in source && ("weak" in source || "least" in source) -> "Пароль слишком простой. Используйте не менее 6 символов."
            "rate limit" in source -> "Слишком много попыток. Подождите немного и повторите."
            "signup is disabled" in source || "signup_disabled" in source -> "Регистрация отключена в настройках Supabase."
            "invalid" in source && "email" in source -> "Supabase отклонил этот email. Проверьте адрес или используйте другой."
            else -> {
                val details = message?.takeIf { it.isNotBlank() && !it.startsWith("Ошибка Supabase") }
                if (details != null) "Ответ Supabase: $details"
                else if (isLogin) "Не удалось войти. Проверьте данные и подключение."
                else "Не удалось создать аккаунт. Проверьте подключение и повторите."
            }
        }
    }

    private fun request(
        url: String,
        method: String = "GET",
        body: String? = null,
        minimal: Boolean = false,
        accessToken: String? = null
    ): String {
        val request = Request.Builder()
            .url(url)
            .header("apikey", BuildConfig.SUPABASE_KEY)
            .header("Content-Type", "application/json")
        if (accessToken != null) {
            request.header("Authorization", "Bearer $accessToken")
        }
        if (minimal) request.header("Prefer", "return=minimal")
        val requestBody = body?.toRequestBody(jsonMediaType)
        request.method(method, requestBody)
        val httpResponse = NetworkClient.httpClient.newCall(request.build()).execute()
        val code = httpResponse.code
        val response = httpResponse.body?.string().orEmpty()
        httpResponse.close()
        if (code !in 200..299) {
            val message = runCatching {
                val json = JSONObject(response)
                json.optString("msg")
                    .ifBlank { json.optString("message") }
                    .ifBlank { json.optString("error_description") }
                    .ifBlank { json.optString("error") }
                    .ifBlank { json.optString("code") }
            }
                .getOrDefault("")
                .ifBlank { "Ошибка Supabase ($code)" }
            error(message)
        }
        return response
    }
}

private fun JSONArray.toStrings(): List<String> {
    return List(length()) { optString(it) }
}
