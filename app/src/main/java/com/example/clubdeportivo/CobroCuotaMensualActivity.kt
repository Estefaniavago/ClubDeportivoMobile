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

        // Listener para el botón de búsqueda (Validar)
        btnValidar.setOnClickListener {
            val dni = etDocumento.text.toString().trim()
            if (dni.isNotBlank()) {
                // Buscamos al cliente completo en la BD
                clienteEncontrado = dbHelper.obtenerClienteCompletoPorDni(dni)

                if (clienteEncontrado != null) {
                    // Si lo encontramos, mostramos el panel de información
                    layoutPago.visibility = View.VISIBLE
                    // Y actualizamos la UI con sus datos
                    actualizarInfoSocioUI()
                } else {
                    // Si no, ocultamos el panel y mostramos un error
                    layoutPago.visibility = View.GONE
                    Toast.makeText(this, "Cliente con DNI $dni no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para el botón de registrar pago
        btnPagar.setOnClickListener {
            clienteEncontrado?.let { cliente ->
                if (dbHelper.registrarPagoSocio(cliente.id)) {
                    Toast.makeText(this, "Pago registrado con éxito para ${cliente.nombre}", Toast.LENGTH_LONG).show()
                    // MUY IMPORTANTE: Refrescamos la UI para mostrar el nuevo estado "Activo"
                    actualizarInfoSocioUI()
                } else {
                    Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
                }
            }
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