package com.example.clubdeportivo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.clubdeportivo.models.Cliente
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.ArrayAdapter
import java.text.SimpleDateFormat
import java.util.Date


class CobroCuotaMensualActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper
    // En lugar de guardar solo el ID, guardamos el objeto Cliente completo
    private var clienteEncontrado: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro_cuota_mensual)

        dbHelper = UserDBHelper(this)

        // Referencias a la UI
        val etDocumento = findViewById<EditText>(R.id.etDocumentoCuota)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val layoutPago = findViewById<LinearLayout>(R.id.layoutPago)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val btnVolver = findViewById<Button>(R.id.button7)
        val radioGroupPago = findViewById<RadioGroup>(R.id.radioGroupPagoCuota)
        val radioEfectivo = findViewById<RadioButton>(R.id.radioEfectivoCuota)
        val radioCredito = findViewById<RadioButton>(R.id.radioCreditoCuota)
        val layoutCuotas = findViewById<LinearLayout>(R.id.layoutCuotas)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinnerCuotas)
        val layoutMedioPago = findViewById<LinearLayout>(R.id.layoutMedioPago)
        val layoutMonto = findViewById<LinearLayout>(R.id.layoutMonto)
        val layoutComprobante = findViewById<LinearLayout>(R.id.layoutComprobanteCuota)
        val tvComprobanteNombre = findViewById<TextView>(R.id.tvComprobanteNombreCuota)
        val tvComprobanteDni = findViewById<TextView>(R.id.tvComprobanteDniCuota)
        val tvComprobanteFecha = findViewById<TextView>(R.id.tvComprobanteFechaCuota)
        val tvComprobanteMedioPago = findViewById<TextView>(R.id.tvComprobanteMedioPagoCuota)
        val btnAceptarComprobante = findViewById<Button>(R.id.btnAceptarComprobanteCuota)
        layoutMonto.visibility = View.GONE

        // Configurar Spinner de cuotas
        val cuotasOptions = listOf("1 cuota", "3 cuotas", "6 cuotas")
        val cuotasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cuotasOptions)
        cuotasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCuotas.adapter = cuotasAdapter

        // Estado de selección
        var medioPagoSeleccionado: String? = null
        var cuotasSeleccionadas: String? = null

        // Listener para el RadioGroup
        radioGroupPago.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioEfectivoCuota -> {
                    medioPagoSeleccionado = "Efectivo"
                    layoutCuotas.visibility = View.GONE
                    cuotasSeleccionadas = null
                    btnPagar.isEnabled = true
                }
                R.id.radioCreditoCuota -> {
                    medioPagoSeleccionado = "Tarjeta de crédito"
                    layoutCuotas.visibility = View.VISIBLE
                    btnPagar.isEnabled = false // Solo se habilita al elegir cuotas
                }
                else -> {
                    btnPagar.isEnabled = false
                }
            }
        }

        // Listener para el Spinner de cuotas
        spinnerCuotas.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (radioCredito.isChecked) {
                    cuotasSeleccionadas = cuotasOptions[position]
                    btnPagar.isEnabled = true
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                cuotasSeleccionadas = null
                btnPagar.isEnabled = false
            }
        })

        // Inicialmente deshabilitar el botón de pago
        btnPagar.isEnabled = false

        // Listener para el botón de búsqueda (Validar)
        btnValidar.setOnClickListener {
            val dni = etDocumento.text.toString().trim()
            if (dni.isNotBlank()) {
                // Buscamos al cliente completo en la BD
                clienteEncontrado = dbHelper.obtenerClienteCompletoPorDni(dni)

                if (clienteEncontrado != null) {
                    // Si lo encontramos, mostramos el panel de información
                    layoutPago.visibility = View.VISIBLE
                    layoutMedioPago.visibility = View.VISIBLE
                    layoutMonto.visibility = View.VISIBLE
                    btnValidar.visibility = View.GONE // Ocultar botón validar
                    // Y actualizamos la UI con sus datos
                    actualizarInfoSocioUI()
                } else {
                    // Si no, ocultamos el panel y mostramos un error
                    layoutPago.visibility = View.GONE
                    layoutMedioPago.visibility = View.GONE
                    layoutMonto.visibility = View.GONE
                    btnValidar.visibility = View.VISIBLE // Mostrar botón validar
                    Toast.makeText(this, "Cliente con DNI $dni no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para el botón de registrar pago
        btnPagar.setOnClickListener {
            if (medioPagoSeleccionado == null || (medioPagoSeleccionado == "Tarjeta de crédito" && cuotasSeleccionadas == null)) {
                Toast.makeText(this, "Seleccione un medio de pago y cuotas si corresponde", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            clienteEncontrado?.let { cliente ->
                if (dbHelper.registrarPagoSocio(cliente.id)) {
                    // Mostrar comprobante
                    val nombreCompleto = "${cliente.nombre} ${cliente.apellido}"
                    val dni = cliente.dni
                    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())
                    val medio = if (medioPagoSeleccionado == "Tarjeta de crédito" && cuotasSeleccionadas != null) {
                        "${medioPagoSeleccionado} - ${cuotasSeleccionadas}"
                    } else {
                        medioPagoSeleccionado
                    }
                    tvComprobanteNombre.text = "Nombre y Apellido: $nombreCompleto"
                    tvComprobanteDni.text = "DNI: $dni"
                    tvComprobanteFecha.text = "Fecha: $fecha"
                    tvComprobanteMedioPago.text = "Medio de Pago: $medio"
                    // Ocultar formulario y mostrar comprobante
                    layoutPago.visibility = View.GONE
                    layoutMedioPago.visibility = View.GONE
                    layoutMonto.visibility = View.GONE
                    layoutComprobante.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnAceptarComprobante.setOnClickListener {
            finish()
        }

        btnVolver.setOnClickListener {
            finish()
        }

        mostrarNombreUsuarioEnEncabezado()
    }

    /**
     * Esta función se encarga de toda la lógica de actualizar la interfaz
     * con la información del cliente encontrado.
     */
    private fun actualizarInfoSocioUI() {
        // Referencias a los componentes dentro del panel de pago
        val tvNombreCliente = findViewById<TextView>(R.id.tvNombreClientePago)
        val tvEstadoCliente = findViewById<TextView>(R.id.tvEstadoClientePago)
        val btnPagar = findViewById<Button>(R.id.btnPagar)

        // Nos aseguramos de que haya un cliente encontrado
        clienteEncontrado?.let { cliente ->
            // Mostramos el nombre del cliente
            tvNombreCliente.text = "Cliente: ${cliente.nombre} ${cliente.apellido}"

            // Verificamos el último estado del socio para dar info detallada
            val ultimoEstado = dbHelper.obtenerUltimoEstadoSocio(cliente.id)

            if (ultimoEstado == null) {
                // Si es null, es porque nunca ha pagado, por lo tanto no es socio.
                tvEstadoCliente.text = "Estado: No es socio"
                btnPagar.isEnabled = true // Puede pagar
            } else {
                // Si tiene un estado, puede ser "Activo" o "Vencido"
                val (estado, vencimiento) = ultimoEstado
                tvEstadoCliente.text = "Estado: $estado (Vence: $vencimiento)"

                // El botón de pago solo se habilita si su estado NO es "Activo"
                btnPagar.isEnabled = (estado != "Activo")
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