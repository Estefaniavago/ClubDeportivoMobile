package com.example.clubdeportivo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu)

        val btnRegistrarCliente = findViewById<Button>(R.id.button2)
        val btnCobroCuota = findViewById<Button>(R.id.button3)
        val btnCobroActividad = findViewById<Button>(R.id.button4)
        val btnListarVencimiento = findViewById<Button>(R.id.button5)
        val btnEmitirCarnet = findViewById<Button>(R.id.button6)
        val btnRegistros = findViewById<Button>(R.id.button8)
        val btnVolver = findViewById<Button>(R.id.button7)

        btnRegistrarCliente.setOnClickListener {
            val intent = Intent(this, RegistroUsuario::class.java)
            startActivity(intent)
        }

        btnCobroCuota.setOnClickListener {
            val intent = Intent(this, CobroCuotaMensualActivity::class.java)
            startActivity(intent)
        }

        btnCobroActividad.setOnClickListener {
            val intent = Intent(this, CobroActividadActivity::class.java)
            startActivity(intent)
        }

        btnListarVencimiento.setOnClickListener {
            val intent = Intent(this, ListarVencimientoActivity::class.java)
            startActivity(intent)
        }

        btnEmitirCarnet.setOnClickListener {
            val intent = Intent(this, EmitirCarnetActivity::class.java)
            startActivity(intent)
        }

        btnRegistros.setOnClickListener {
            val intent = Intent(this, RegistrosActivity::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menubotones)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mostrarNombreUsuarioEnEncabezado()
    }
    private fun mostrarNombreUsuarioEnEncabezado() {
        val sharedPrefs = getSharedPreferences("ClubDeportivoPrefs", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPrefs.getString("NOMBRE_USUARIO_LOGUEADO", "Usuario")
        val tvUsuario = findViewById<TextView>(R.id.textView4)
        tvUsuario?.text = nombreUsuario
    }
}