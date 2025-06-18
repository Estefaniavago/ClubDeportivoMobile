package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebaclubdeportivo.UserDBHelper

class PantallaIngreso : AppCompatActivity() {
    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_ingreso)

        // Inicializar el helper de la base de datos existente
        dbHelper = UserDBHelper(this)

        val editTextUsuario = findViewById<EditText>(R.id.etUsername)
        val editTextPassword = findViewById<EditText>(R.id.etPassword)
        val btnIngresoAdmin = findViewById<Button>(R.id.btnIngresoAdm)
        val btnVolver = findViewById<Button>(R.id.btnSalir2)

        btnIngresoAdmin.setOnClickListener {
            val usuario = editTextUsuario.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Verificar que los campos no estén vacíos
            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar las credenciales usando el UserDBHelper existente
            if (dbHelper.login(usuario, password)) {
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

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}