package com.adriang.urchefmate.login.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    // Guardar el correo y la contraseña del usuario
    fun saveUser(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.putString("user_password", password) // Guardar la contraseña
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    // Obtener el correo del usuario
    fun getUser(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    // Obtener la contraseña del usuario
    fun getUserPassword(): String? {
        return sharedPreferences.getString("user_password", null)
    }

    // Verificar si el usuario está logueado
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    // Cerrar sesión
    fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}