package com.example.culinaryassistant.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinaryassistant.ui.components.AppField
import com.example.culinaryassistant.ui.components.OrangeButton
import com.example.culinaryassistant.model.AuthResult
import com.example.culinaryassistant.ui.theme.AppMuted
import com.example.culinaryassistant.ui.theme.AppOrange

@Composable
fun LoginScreen(onRegister: () -> Unit, onLogin: (String, String, (AuthResult) -> Unit) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    AuthContainer {
        Text("Вход", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(30.dp))
        AppField(email, { email = it }, "Email", KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AppField(password, { password = it }, "Пароль", password = true)
        Spacer(Modifier.height(30.dp))
        OrangeButton(if (busy) "Входим..." else "Войти", !busy && email.isNotBlank() && password.isNotBlank()) {
            busy = true
            error = null
            onLogin(email.trim(), password) { result -> busy = false; error = result.message }
        }
        error?.let { Text(it, color = AppOrange, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp)) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Нет аккаунта?", color = AppMuted, fontSize = 12.sp)
            TextButton(onClick = onRegister) { Text("Зарегистрироваться", color = AppOrange, fontSize = 12.sp) }
        }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, onRegister: (String, String, String, (AuthResult) -> Unit) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    var accepted by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf(false) }
    AuthContainer {
        Text("Регистрация", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(26.dp))
        AppField(name, { name = it }, "Имя")
        Spacer(Modifier.height(10.dp))
        AppField(email, { email = it }, "Email", KeyboardType.Email)
        Spacer(Modifier.height(10.dp))
        AppField(password, { password = it }, "Пароль — минимум 8 символов", password = true)
        Spacer(Modifier.height(10.dp))
        AppField(repeat, { repeat = it }, "Повторите пароль", password = true)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().clickable { accepted = !accepted }, verticalAlignment = Alignment.CenterVertically) {
            Icon(if (accepted) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank, null, tint = AppOrange, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Я согласен с условиями", color = AppMuted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(24.dp))
        OrangeButton(
            if (busy) "Создаём..." else "Зарегистрироваться",
            !busy && accepted && name.isNotBlank() && email.isNotBlank() && password.length >= 8 && password == repeat
        ) {
            busy = true
            message = null
            successMessage = false
            onRegister(name.trim(), email.trim(), password) { result ->
                busy = false
                message = result.message
                successMessage = result.registrationPending
            }
        }
        message?.let {
            Text(
                it,
                color = if (successMessage) androidx.compose.ui.graphics.Color(0xFF3E8E50) else AppOrange,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Уже есть аккаунт?", color = AppMuted, fontSize = 12.sp)
            TextButton(onClick = onBack) { Text("Войти", color = AppOrange, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun AuthContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().imePadding().verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}
