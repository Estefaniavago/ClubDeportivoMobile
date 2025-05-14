package com.example.clubdeportivo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast


class RegistroUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etNumeroDocumento = findViewById<EditText>(R.id.etNumeroDocumento)
        val cbAptoFisico = findViewById<CheckBox>(R.id.cbAptoFisico)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val apellido = etApellido.text.toString()
            val documento = etNumeroDocumento.text.toString()
            val aptoFisico = cbAptoFisico.isChecked

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && documento.isNotEmpty()) {
                // Aquí iría la lógica para registrar el cliente
                Toast.makeText(this, "Cliente registrado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            etApellido.text.clear()
            etNumeroDocumento.text.clear()
            cbAptoFisico.isChecked = false
        }
    }
}