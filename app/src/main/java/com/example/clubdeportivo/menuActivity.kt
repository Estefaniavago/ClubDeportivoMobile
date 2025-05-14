package com.example.clubdeportivo

import android.os.Bundle
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


        btnRegistrarCliente.setOnClickListener {

            val intent = Intent(this, RegistroUsuario::class.java)


            startActivity(intent)


            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menubotones)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}