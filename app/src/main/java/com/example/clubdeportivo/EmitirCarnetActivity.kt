package com.example.clubdeportivo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.clubdeportivo.models.Cliente

class EmitirCarnetActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emitir_carnet)

        dbHelper = UserDBHelper(this)

        // Referencias a los componentes de la UI
        val etDniBusqueda = findViewById<EditText>(R.id.etDniBusquedaCarnet)
        val btnValidar = findViewById<Button>(R.id.btnValidarCarnet)
        val cardCarnet = findViewById<CardView>(R.id.cardCarnet)
        val btnEmitir = findViewById<Button>(R.id.btnEmitir)
        val btnVolver = findViewById<Button>(R.id.btnVolverCarnet)

        // Listener del botón para validar el DNI
        btnValidar.setOnClickListener {
            val dni = etDniBusqueda.text.toString().trim()
            if (dni.isNotBlank()) {
                // Buscamos al cliente en la base de datos
                val cliente = dbHelper.obtenerClienteCompletoPorDni(dni)
                if (cliente != null) {
                    // Si lo encontramos, mostramos y rellenamos el carnet
                    mostrarCarnet(cliente, cardCarnet, btnEmitir)
                } else {
                    // Si no, ocultamos el carnet y mostramos un error
                    cardCarnet.visibility = View.GONE
                    btnEmitir.isEnabled = false
                    Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmitir.setOnClickListener {
            // Aquí iría la lógica futura para imprimir o guardar como PDF
            Toast.makeText(this, "Carnet generado (simulación)", Toast.LENGTH_SHORT).show()
        }

        btnVolver.setOnClickListener {
            finish()
        }

        mostrarNombreUsuarioEnEncabezado()
    }

    private fun mostrarCarnet(cliente: Cliente, cardView: CardView, btnEmitir: Button) {
        // Hacemos visible el carnet
        cardView.visibility = View.VISIBLE
        btnEmitir.isEnabled = true

        // Obtenemos las referencias a los TextViews dentro del carnet
        val tvNombre = cardView.findViewById<TextView>(R.id.tvNombreCarnet)
        val tvDni = cardView.findViewById<TextView>(R.id.tvDniCarnet)
        val tvEstado = cardView.findViewById<TextView>(R.id.tvEstadoCarnet)

        // Rellenamos los datos del cliente
        tvNombre.text = "${cliente.nombre} ${cliente.apellido}"
        tvDni.text = "DNI: ${cliente.dni}"

        // --- Lógica para determinar el estado del socio ---
        val esActivo = dbHelper.esSocioActivo(cliente.id)
        if (esActivo) {
            tvEstado.text = "SOCIO ACTIVO"
            tvEstado.setTextColor(Color.parseColor("#4CAF50")) // Verde
        } else {
            // Si no es activo, verificamos si alguna vez fue socio para saber si está vencido
            val ultimoEstado = dbHelper.obtenerUltimoEstadoSocio(cliente.id)
            if (ultimoEstado != null) {
                tvEstado.text = "SOCIO VENCIDO"
                tvEstado.setTextColor(Color.parseColor("#F44336")) // Rojo
            } else {
                tvEstado.text = "NO SOCIO"
                tvEstado.setTextColor(Color.parseColor("#FF9800")) // Naranja
            }
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    private fun mostrarNombreUsuarioEnEncabezado() {
        val sharedPrefs = getSharedPreferences("ClubDeportivoPrefs", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPrefs.getString("NOMBRE_USUARIO_LOGUEADO", "Usuario")
        val tvUsuario = findViewById<TextView>(R.id.textView4)
        tvUsuario?.text = nombreUsuario
    }

}