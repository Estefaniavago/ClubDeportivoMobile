package com.example.clubdeportivo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.clubdeportivo.UserDBHelper

class RegistroUsuario : AppCompatActivity() {
    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        dbHelper = UserDBHelper(this)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etNumeroDocumento = findViewById<EditText>(R.id.etNumeroDocumento)
        val cbAptoFisico = findViewById<CheckBox>(R.id.cbAptoFisico)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val documento = etNumeroDocumento.text.toString().trim()
            val aptoFisico = cbAptoFisico.isChecked

            if (nombre.isNotBlank() && apellido.isNotBlank() && documento.isNotBlank()) {
                if (dbHelper.insertarCliente(nombre, apellido, documento, aptoFisico)) {
                    Toast.makeText(this, "Cliente registrado exitosamente", Toast.LENGTH_SHORT).show()
                    finish() // Volver a la pantalla anterior
                } else {
                    Toast.makeText(this, "Error al registrar el cliente. El DNI podría estar duplicado.", Toast.LENGTH_SHORT).show()
                }
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

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}