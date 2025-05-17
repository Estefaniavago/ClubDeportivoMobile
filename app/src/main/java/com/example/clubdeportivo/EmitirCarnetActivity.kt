package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmitirCarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emitir_carnet)

        val etDNI = findViewById<EditText>(R.id.etDNI)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etVencimiento = findViewById<EditText>(R.id.etVencimiento)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val btnEmitir = findViewById<Button>(R.id.btnEmitir)
        val btnVolver = findViewById<Button>(R.id.button7)

        btnValidar.setOnClickListener {
            if (etDNI.text.isNotEmpty()) {
                // Simulamos encontrar datos del socio
                etNombre.setText("Juan")
                etApellido.setText("Pérez")
                etVencimiento.setText("01/05/2025")
                btnEmitir.isEnabled = true
                Toast.makeText(this, "Socio encontrado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmitir.setOnClickListener {
            Toast.makeText(this, "Carnet generado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
} 