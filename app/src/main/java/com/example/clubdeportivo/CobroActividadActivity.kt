package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebaclubdeportivo.UserDBHelper

class CobroActividadActivity : AppCompatActivity() {
    private lateinit var dbHelper: UserDBHelper
    private var clienteId: Long = -1

    // Variables para el chequeo de habilitación del botón pagar
    private var actividadSeleccionada: String? = null
    private var precioSeleccionado: Int? = null
    private var horarioSeleccionado: String? = null
    private var medioPagoSeleccionado: String? = null
    private var btnPagarFinal: Button? = null
    private var dniValidado: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro_actividad)

        dbHelper = UserDBHelper(this)

        // Referencias a las vistas
        val etDNI = findViewById<EditText>(R.id.etDNI)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val etNombreApellido = findViewById<EditText>(R.id.etNombreApellido)
        val spinnerActividad = findViewById<Spinner>(R.id.spinnerActividad)
        val etPrecio = findViewById<EditText>(R.id.etPrecio)
        val listViewHorarios = findViewById<ListView>(R.id.listViewHorarios)
        val radioGroupPago = findViewById<RadioGroup>(R.id.radioGroupPago)
        val btnVolver = findViewById<Button>(R.id.button7)
        btnPagarFinal = findViewById(R.id.btnPagarFinal)
        btnPagarFinal?.isEnabled = false

        // Referencias al comprobante
        val layoutComprobante = findViewById<LinearLayout>(R.id.layoutComprobante)
        val tvComprobanteNombre = findViewById<TextView>(R.id.tvComprobanteNombre)
        val tvComprobanteDni = findViewById<TextView>(R.id.tvComprobanteDni)
        val tvComprobanteActividad = findViewById<TextView>(R.id.tvComprobanteActividad)
        val tvComprobanteMonto = findViewById<TextView>(R.id.tvComprobanteMonto)
        val tvComprobanteMedioPago = findViewById<TextView>(R.id.tvComprobanteMedioPago)
        val tvComprobanteFecha = findViewById<TextView>(R.id.tvComprobanteFecha)
        val btnAceptarComprobante = findViewById<Button>(R.id.btnAceptarComprobante)
        val btnImprimirComprobante = findViewById<Button>(R.id.btnImprimirComprobante)

        // Layout principal del formulario (todo menos el comprobante)
        val layoutFormulario = findViewById<LinearLayout>(R.id.layoutFormulario)

        // Configurar spinner de actividades
        val actividades = arrayOf("Gimnasio", "Natación", "Fútbol", "Básquet", "Tenis")
        val adapterActividades = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividades)
        adapterActividades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapterActividades

        // Configurar lista de horarios
        val horarios = arrayOf(
            "Lunes - 9:00 a 10:00",
            "Martes - 15:00 a 16:00",
            "Miércoles - 18:00 a 19:00",
            "Jueves - 10:00 a 11:00"
        )
        val adapterHorarios = ArrayAdapter(this, android.R.layout.simple_list_item_1, horarios)
        listViewHorarios.adapter = adapterHorarios

        // Inicialmente deshabilitar campos hasta validar
        etNombreApellido.isEnabled = false
        spinnerActividad.isEnabled = false
        etPrecio.isEnabled = true
        etPrecio.isFocusable = false
        etPrecio.isClickable = false
        listViewHorarios.isEnabled = false
        radioGroupPago.isEnabled = false
        btnPagarFinal?.isEnabled = false

        // Habilitar selección de horario
        listViewHorarios.setOnItemClickListener { _, _, position, _ ->
            horarioSeleccionado = horarios[position]
            chequearHabilitarPagar()
        }

        // Habilitar selección de medio de pago
        radioGroupPago.setOnCheckedChangeListener { _, checkedId ->
            medioPagoSeleccionado = when (checkedId) {
                R.id.radioEfectivo -> "Efectivo"
                R.id.radioCredito -> "Crédito"
                else -> null
            }
            chequearHabilitarPagar()
        }

        spinnerActividad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val precios = arrayOf("2000", "2500", "1800", "1800", "2200")
                etPrecio.setText("$${precios[position]}")
                actividadSeleccionada = actividades[position]
                precioSeleccionado = precios[position].toInt()
                chequearHabilitarPagar()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnPagarFinal?.setOnClickListener {
            if (!dniValidado) {
                Toast.makeText(this, "Debe validar el DNI primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (actividadSeleccionada == null || precioSeleccionado == null || medioPagoSeleccionado == null) {
                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val exito = dbHelper.registrarPagoActividadDiaria(
                clienteId,
                actividadSeleccionada!!,
                precioSeleccionado!!,
                horarioSeleccionado ?: "",
                medioPagoSeleccionado!!
            )
            if (exito) {
                // Ocultar formulario y mostrar comprobante
                layoutFormulario.visibility = View.GONE
                layoutComprobante.visibility = View.VISIBLE
                // Obtener nombre y apellido
                val nombreApellido = etNombreApellido.text.toString()
                val dni = etDNI.text.toString()
                val actividad = actividadSeleccionada!!
                val monto = precioSeleccionado!!
                val medioPago = medioPagoSeleccionado!!
                val fecha = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                tvComprobanteNombre.text = "Nombre y Apellido: $nombreApellido"
                tvComprobanteDni.text = "DNI: $dni"
                tvComprobanteActividad.text = "Actividad: $actividad"
                tvComprobanteMonto.text = "Monto: $$monto"
                tvComprobanteMedioPago.text = "Medio de Pago: $medioPago"
                tvComprobanteFecha.text = "Fecha: $fecha"
            } else {
                Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_LONG).show()
            }
        }

        btnValidar.setOnClickListener {
            val dni = etDNI.text.toString()
            if (dni.isNotEmpty()) {
                validarDNI(dni, etNombreApellido, spinnerActividad, etPrecio, listViewHorarios, radioGroupPago)
            } else {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnAceptarComprobante.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        btnImprimirComprobante.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de impresión próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chequearHabilitarPagar() {
        btnPagarFinal?.isEnabled = dniValidado && actividadSeleccionada != null && medioPagoSeleccionado != null
    }

    private fun validarDNI(
        dni: String,
        etNombreApellido: EditText,
        spinnerActividad: Spinner,
        etPrecio: EditText,
        listViewHorarios: ListView,
        radioGroupPago: RadioGroup
    ) {
        val clienteIdLocal = dbHelper.obtenerClientePorDni(dni)
        if (clienteIdLocal == null) {
            Toast.makeText(this, "El DNI no corresponde a un cliente registrado", Toast.LENGTH_LONG).show()
            dniValidado = false
            chequearHabilitarPagar()
            return
        }
        // Verificar que NO sea socio (no debe existir en la tabla socios)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM socios WHERE cliente_id = ? LIMIT 1", arrayOf(clienteIdLocal.toString()))
        val esSocio = cursor.moveToFirst()
        cursor.close()
        if (esSocio) {
            Toast.makeText(this, "El cliente ya es socio y no puede pagar cuota diaria", Toast.LENGTH_LONG).show()
            dniValidado = false
            chequearHabilitarPagar()
            return
        }
        // Si cumple, habilitar el resto de la UI y mostrar nombre y apellido
        val cursorCliente = db.query("clientes", arrayOf("nombre", "apellido"), "id = ?", arrayOf(clienteIdLocal.toString()), null, null, null)
        if (cursorCliente.moveToFirst()) {
            val nombre = cursorCliente.getString(cursorCliente.getColumnIndexOrThrow("nombre"))
            val apellido = cursorCliente.getString(cursorCliente.getColumnIndexOrThrow("apellido"))
            etNombreApellido.setText("$nombre $apellido")
        }
        cursorCliente.close()
        this.clienteId = clienteIdLocal
        dniValidado = true
        etNombreApellido.isEnabled = false
        spinnerActividad.isEnabled = true
        etPrecio.isEnabled = true
        etPrecio.isFocusable = false
        etPrecio.isClickable = false
        listViewHorarios.isEnabled = true
        radioGroupPago.isEnabled = true
        Toast.makeText(this, "DNI validado correctamente. Puede continuar con el pago diario.", Toast.LENGTH_SHORT).show()
        chequearHabilitarPagar()
    }
} 