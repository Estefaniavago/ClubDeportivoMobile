package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PantallaIngreso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_ingreso)

        val editTextUsuario = findViewById<EditText>(R.id.etUsername)
        val editTextPassword = findViewById<EditText>(R.id.etPassword)
        val btnIngresoAdmin = findViewById<Button>(R.id.btnIngresoAdm)
        val btnVolver = findViewById<Button>(R.id.btnSalir2)

        btnIngresoAdmin.setOnClickListener {
            val usuario = editTextUsuario.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (usuario == "admin" && password == "admin") {
                // Credenciales correctas, navegar al menú
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish() // Cierra esta actividad para que no se pueda volver atrás
            } else {
                // Credenciales incorrectas, mostrar mensaje de error
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish() // Volver a la actividad anterior
        }
    }
}