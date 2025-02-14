package com.adriang.urchefmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.adriang.urchefmate.core.navigation.NavigationWrapper
import com.adriang.urchefmate.login.data.SessionManager
import com.adriang.urchefmate.ui.theme.UrChefMateTheme

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear instancia de SessionManager
        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        setContent {
            UrChefMateTheme() {
                NavigationWrapper(sessionManager)
            }
        }
    }
}