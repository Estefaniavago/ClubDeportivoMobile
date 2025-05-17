package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CobroCuotaMensualActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro_cuota_mensual)

        val etDocumento = findViewById<EditText>(R.id.etDocumentoCuota)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val btnVolver = findViewById<Button>(R.id.button7)

        btnValidar.setOnClickListener {
            if (etDocumento.text.isNotEmpty()) {
                // Aquí iría la validación del DNI
                Toast.makeText(this, "Validando DNI: ${etDocumento.text}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor ingrese un número de documento", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
} 