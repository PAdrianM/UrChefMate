package com.adriang.urchefmate.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriang.urchefmate.R
import com.adriang.urchefmate.login.data.SessionManager
import com.adriang.urchefmate.ui.theme.DarkLetter
import com.adriang.urchefmate.ui.theme.White
import com.adriang.urchefmate.ui.theme.Yellow


@Composable
fun LoginScreen(
    navigateToHome: (String) -> Unit,
    sessionManager: SessionManager
) {
    var user by remember { mutableStateOf("correo@gmail.com") }
    var password by remember { mutableStateOf("pasword123") }
    var showError by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Yellow)
            .padding(top = 0.dp, start = 28.dp, end = 28.dp, bottom = 0.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitlesContainer()
            Spacer(modifier = Modifier.padding(32.dp))
            EmailField(user = user) { user = it }
            Spacer(modifier = Modifier.padding(16.dp))
            PassWordField(password = password) { password = it }
            Spacer(modifier = Modifier.padding(8.dp))
            //ForgotPassword(Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.padding(32.dp))

            if (showError) {
                Text(
                    text = stringResource(id = R.string.warning),
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            LoginButton(
                Modifier.align(Alignment.CenterHorizontally),
                navigateToHome = {
                    if (user == "correo@gmail.com" && password == "pasword123") {
                        showError = false
                        sessionManager.saveUser(user, password)
                        navigateToHome("$user")
                    } else {
                        showError = true
                    }
                }
            )
        }
    }
}


@Composable
fun TitlesContainer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AcronymTitle()
        TitleApp()
    }
}

@Composable
fun AcronymTitle() {
    Text(
        text = stringResource(id = R.string.acronym_title_app),
        style = TextStyle(
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = DarkLetter
        )
    )
}

@Composable
fun TitleApp() {
    Text(
        text = stringResource(id = R.string.title_app),
        style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkLetter
        )
    )
}

@Composable
fun LoginButton(modifier: Modifier, navigateToHome: () -> Unit) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = { navigateToHome() },
        enabled = true,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = White)
    ) {
        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 18.sp,
            color = DarkLetter,
            fontWeight = FontWeight.Bold

        )
    }
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "Forgot Password?",
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = White
    )
}

@Composable
fun EmailField(user: String, onUserChange: (String) -> Unit) {
    TextField(
        value = "correo@gmail.com",
        onValueChange = {  },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "correo@gmail.com") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email Icon",
                tint = DarkLetter
            )
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = DarkLetter,
            unfocusedTextColor = DarkLetter,
            unfocusedContainerColor = Yellow,
            focusedContainerColor = Yellow,
            focusedIndicatorColor = DarkLetter
        )

    )
}

@Composable
fun PassWordField(password: String, onPasswordChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = "pasword123",
        onValueChange = {  },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "pasword123") },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (passwordVisible) KeyboardType.Text else KeyboardType.Password
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Password Icon",
                tint = DarkLetter
            )
        },
        trailingIcon = {
            val imageRes = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = painterResource(id = imageRes),
                    contentDescription = description,
                    tint = DarkLetter
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = DarkLetter,
            unfocusedTextColor = DarkLetter,
            unfocusedContainerColor = Yellow,
            focusedContainerColor = Yellow,
            focusedIndicatorColor = DarkLetter
        )
    )
}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewLoginScreen() {
//    LoginScreen(navigateToHome = {}, sessionManager = SessionManager())
//}
//
