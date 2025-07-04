package com.example.clubdeportivo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivo.UserDBHelper

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

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.login(usuario, password)) {

                // 1. Guardamos el nombre del usuario que inició sesión
                val sharedPrefs = getSharedPreferences("ClubDeportivoPrefs", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                editor.putString("NOMBRE_USUARIO_LOGUEADO", usuario)
                editor.apply() // apply() guarda los cambios
                

                // Credenciales correctas, navegar al menú
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Credenciales incorrectas, mostrar mensaje de error
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}