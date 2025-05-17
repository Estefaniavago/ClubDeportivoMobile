package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CobroActividadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro_actividad)

        // Referencias a las vistas
        val etDNI = findViewById<EditText>(R.id.etDNI)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val etNombreApellido = findViewById<EditText>(R.id.etNombreApellido)
        val spinnerActividad = findViewById<Spinner>(R.id.spinnerActividad)
        val etPrecio = findViewById<EditText>(R.id.etPrecio)
        val listViewHorarios = findViewById<ListView>(R.id.listViewHorarios)
        val radioGroupPago = findViewById<RadioGroup>(R.id.radioGroupPago)
        val spinnerCredito = findViewById<Spinner>(R.id.spinnerCredito)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val btnVolver = findViewById<Button>(R.id.button7)

        // Configurar spinner de actividades
        val actividades = arrayOf("Gimnasio", "Natación", "Fútbol", "Básquet", "Tenis")
        val adapterActividades = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividades)
        adapterActividades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapterActividades

        // Configurar spinner de tarjetas de crédito
        val tarjetas = arrayOf("Visa", "Mastercard", "American Express")
        val adapterTarjetas = ArrayAdapter(this, android.R.layout.simple_spinner_item, tarjetas)
        adapterTarjetas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCredito.adapter = adapterTarjetas

        // Manejar la visibilidad del spinner de tarjetas según la selección del radio button
        radioGroupPago.setOnCheckedChangeListener { _, checkedId ->
            spinnerCredito.visibility = if (checkedId == R.id.radioCredito) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }

        // Configurar lista de horarios
        val horarios = arrayOf(
            "Lunes - 9:00 a 10:00",
            "Martes - 15:00 a 16:00",
            "Miércoles - 18:00 a 19:00",
            "Jueves - 10:00 a 11:00"
        )
        val adapterHorarios = ArrayAdapter(this, android.R.layout.simple_list_item_1, horarios)
        listViewHorarios.adapter = adapterHorarios

        btnValidar.setOnClickListener {
            if (etDNI.text.isNotEmpty()) {
                // Simular validación de DNI
                etNombreApellido.setText("Juan Pérez")
                Toast.makeText(this, "DNI validado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        spinnerActividad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Simular precio según actividad
                val precios = arrayOf("2000", "2500", "1800", "1800", "2200")
                etPrecio.setText("$${precios[position]}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnPagar.setOnClickListener {
            val medioPago = if (radioGroupPago.checkedRadioButtonId == R.id.radioEfectivo) "Efectivo" else "Crédito"
            Toast.makeText(this, "Pago procesado con $medioPago", Toast.LENGTH_SHORT).show()
            // Volver al menú después del pago
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnVolver.setOnClickListener {
            // Volver al menú
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
} 