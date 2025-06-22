package com.example.clubdeportivo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivo.models.Actividad
import com.example.clubdeportivo.models.Cliente
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CobroActividadActivity : AppCompatActivity() {
    private lateinit var dbHelper: UserDBHelper
    private var clienteEncontrado: Cliente? = null

    // Guardaremos la lista de actividades cargadas desde la BD
    private lateinit var listaDeActividades: List<Actividad>
    // Guardaremos la actividad que el usuario seleccione
    private var actividadSeleccionada: Actividad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro_actividad)

        dbHelper = UserDBHelper(this)

        // --- Referencias a Vistas ---
        val etDNI = findViewById<EditText>(R.id.etDNI)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val etNombreApellido = findViewById<EditText>(R.id.etNombreApellido)
        val spinnerActividad = findViewById<Spinner>(R.id.spinnerActividad)
        val etPrecio = findViewById<EditText>(R.id.etPrecio)
        val listViewHorarios = findViewById<ListView>(R.id.listViewHorarios) // Cambiaremos ListView por TextView para simplificar
        val radioGroupPago = findViewById<RadioGroup>(R.id.radioGroupPago)
        val btnPagarFinal = findViewById<Button>(R.id.btnPagarFinal)
        val btnVolver = findViewById<Button>(R.id.button7)

        // --- Lógica Principal ---

        // 1. Cargamos las actividades desde la BD
        cargarActividadesEnSpinner(spinnerActividad)

        // 2. Configuramos los listeners
        btnValidar.setOnClickListener {
            val dni = etDNI.text.toString().trim()
            if (dni.isNotBlank()) {
                validarCliente(dni)
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        spinnerActividad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtenemos el objeto Actividad completo que el usuario seleccionó
                actividadSeleccionada = listaDeActividades[position]

                // Actualizamos el precio en la UI
                actividadSeleccionada?.let { act ->
                    etPrecio.setText(String.format(Locale.US, "%.2f", act.precio))


                    // Verificamos si la actividad tiene horarios definidos
                    if (!act.horarios.isNullOrBlank()) {
                        // Partimos el string de horarios por la coma para obtener una lista
                        val listaDeHorarios = act.horarios.split(',').map { it.trim() }

                        // Creamos un nuevo adaptador para la ListView con esos horarios
                        val horariosAdapter = ArrayAdapter(this@CobroActividadActivity,
                            android.R.layout.simple_list_item_single_choice, listaDeHorarios)

                        // Asignamos el nuevo adaptador a la ListView
                        listViewHorarios.adapter = horariosAdapter
                        listViewHorarios.clearChoices() // Limpiamos la selección anterior
                    } else {
                        // Si no hay horarios, vaciamos la lista
                        listViewHorarios.adapter = null
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                actividadSeleccionada = null
            }
        }

        btnPagarFinal.setOnClickListener {
            realizarPago()
        }

        mostrarNombreUsuarioEnEncabezado()
    }

    private fun cargarActividadesEnSpinner(spinner: Spinner) {
        // Obtenemos la lista de actividades desde la base de datos
        listaDeActividades = dbHelper.obtenerTodasLasActividades()

        // Extraemos solo los nombres para mostrarlos en el Spinner
        val nombresActividades = listaDeActividades.map { it.nombre }

        // Creamos un adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresActividades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun validarCliente(dni: String) {
        clienteEncontrado = dbHelper.obtenerClienteCompletoPorDni(dni)

        if (clienteEncontrado == null) {
            Toast.makeText(this, "El DNI no corresponde a un cliente registrado", Toast.LENGTH_LONG).show()
            return
        }

        // Verificamos si es socio activo
        if (dbHelper.esSocioActivo(clienteEncontrado!!.id)) {
            Toast.makeText(this, "El cliente ya es socio y no puede pagar actividad diaria", Toast.LENGTH_LONG).show()
            return
        }

        // Si todo está OK, mostramos los datos y habilitamos los campos
        findViewById<EditText>(R.id.etNombreApellido).setText("${clienteEncontrado!!.nombre} ${clienteEncontrado!!.apellido}")
        Toast.makeText(this, "Cliente validado. Puede continuar.", Toast.LENGTH_SHORT).show()

        // Habilitar los controles de la UI
        findViewById<Spinner>(R.id.spinnerActividad).isEnabled = true
        findViewById<RadioGroup>(R.id.radioGroupPago).isEnabled = true
        findViewById<Button>(R.id.btnPagarFinal).isEnabled = true
    }

    private fun realizarPago() {
        // 1. Validamos que haya un cliente
        val cliente = clienteEncontrado ?: run {
            Toast.makeText(this, "Valide un DNI primero", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validamos que haya una actividad seleccionada
        val actividad = actividadSeleccionada ?: run {
            Toast.makeText(this, "Seleccione una actividad", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Obtenemos el horario seleccionado de la ListView
        val listViewHorarios = findViewById<ListView>(R.id.listViewHorarios)
        val posicionHorario = listViewHorarios.checkedItemPosition
        val horarioSeleccionado: String

        if (posicionHorario != ListView.INVALID_POSITION) {
            horarioSeleccionado = listViewHorarios.getItemAtPosition(posicionHorario) as String
        } else {
            // Si la actividad tiene horarios, es obligatorio seleccionar uno
            if (!actividad.horarios.isNullOrBlank()) {
                Toast.makeText(this, "Seleccione un horario", Toast.LENGTH_SHORT).show()
                return
            } else {
                horarioSeleccionado = "" // Si no hay horarios, lo dejamos vacío
            }
        }

        // 4. Obtenemos el medio de pago
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupPago)
        val medioDePago = when (radioGroup.checkedRadioButtonId) {
            R.id.radioEfectivo -> "Efectivo"
            R.id.radioCredito -> "Crédito"
            else -> {
                Toast.makeText(this, "Seleccione un medio de pago", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // 5. Si todas las validaciones pasan, registramos el pago
        val exito = dbHelper.registrarPagoActividadDiaria(
            cliente.id,
            actividad.nombre,
            actividad.precio.toInt(), // Convertimos a Int para la BD
            horarioSeleccionado,
            medioDePago
        )

        if (exito) {
            Toast.makeText(this, "Pago registrado con éxito", Toast.LENGTH_LONG).show()
            // Aquí podrías mostrar tu comprobante antes de cerrar
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_LONG).show()
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