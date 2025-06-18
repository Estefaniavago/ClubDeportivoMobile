package com.example.clubdeportivo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebaclubdeportivo.UserDBHelper

class CobroCuotaMensualActivity : AppCompatActivity() {
    private lateinit var dbHelper: UserDBHelper
    private var clienteId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro_cuota_mensual)

        dbHelper = UserDBHelper(this)

        val etDocumento = findViewById<EditText>(R.id.etDocumentoCuota)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val btnVolver = findViewById<Button>(R.id.button7)
        val layoutPago = findViewById<LinearLayout>(R.id.layoutPago)

        btnValidar.setOnClickListener {
            if (etDocumento.text.isNotEmpty()) {
                val dni = etDocumento.text.toString()
                validarDNI(dni, layoutPago)
            } else {
                Toast.makeText(this, "Por favor ingrese un número de documento", Toast.LENGTH_SHORT).show()
            }
        }

        btnPagar.setOnClickListener {
            if (clienteId != -1L) {
                realizarPago()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun validarDNI(dni: String, layoutPago: LinearLayout) {
        // Obtener el ID del cliente por DNI
        val posibleClienteId = dbHelper.obtenerClientePorDni(dni)

        if (posibleClienteId == null) {
            // El DNI no corresponde a un cliente registrado
            Toast.makeText(this, 
                "El DNI no corresponde a un cliente registrado en el sistema", 
                Toast.LENGTH_LONG).show()
            layoutPago.visibility = View.GONE
            clienteId = -1L
            return
        }

        // Verificar si es socio activo
        if (dbHelper.esSocioActivo(posibleClienteId)) {
            Toast.makeText(this, 
                "El cliente ya es socio activo con cuota al día", 
                Toast.LENGTH_LONG).show()
            layoutPago.visibility = View.GONE
            clienteId = -1L
            return
        }

        // Si llegamos aquí, el cliente existe y no es socio activo o tiene la cuota vencida
        clienteId = posibleClienteId
        layoutPago.visibility = View.VISIBLE
        Toast.makeText(this, "DNI validado correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun realizarPago() {
        if (dbHelper.registrarPagoSocio(clienteId)) {
            Toast.makeText(this, 
                "Pago registrado correctamente. ¡Bienvenido al club!", 
                Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, 
                "Error al registrar el pago. Por favor, intente nuevamente", 
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
} 