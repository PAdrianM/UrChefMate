package com.adriang.urchefmate.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adriang.urchefmate.R
import com.adriang.urchefmate.home.ui.BottomNavigationBar
import com.adriang.urchefmate.login.data.SessionManager
import com.adriang.urchefmate.ui.theme.DarkGray
import com.adriang.urchefmate.ui.theme.DarkLetter
import com.adriang.urchefmate.ui.theme.LightGray
import com.adriang.urchefmate.ui.theme.White
import com.adriang.urchefmate.ui.theme.Yellow

@Composable
fun ProfileScreen(navController: NavController, sessionManager: SessionManager) {
    val userEmail = sessionManager.getUser() ?: "No user"
    val userName = userEmail.substringBefore("@")
    val userPassword = sessionManager.getUserPassword() ?: "No disponible"

    // Estado para controlar si la contraseña es visible o no
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Imagen de perfil redondeada
            ProfileImage(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.padding(16.dp))


            ProfileField(label = stringResource(id = R.string.namre_profile), value = userName)

            Spacer(modifier = Modifier.padding(16.dp))


            ProfileField(label = stringResource(id = R.string.email_profile), value = userEmail)

            Spacer(modifier = Modifier.padding(16.dp))


            PasswordField(
                label = stringResource(id = R.string.password_profile),
                password = userPassword,
                isPasswordVisible = isPasswordVisible,
                onToggleVisibility = { isPasswordVisible = !isPasswordVisible }
            )

            Spacer(modifier = Modifier.padding(32.dp))

            // Botón para cerrar sesión
            LogoutButton(
                onClick = {
                    sessionManager.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.avatar_male),
        contentDescription = "Profile Image",
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(LightGray)
    )
}

@Composable
fun ProfileField(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 22.sp,
            color = DarkLetter
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGray
        )
    }
}

@Composable
fun PasswordField(
    label: String,
    password: String,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 22.sp,
            color = DarkLetter
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isPasswordVisible) password else stringResource(id = R.string.not_password),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onToggleVisibility
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isPasswordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    ),
                    contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = DarkLetter
                )
            }
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Yellow)
    ) {
        Text(
            text = stringResource(id = R.string.close_session),
            fontSize = 16.sp,
            color = DarkLetter
        )
    }
}
