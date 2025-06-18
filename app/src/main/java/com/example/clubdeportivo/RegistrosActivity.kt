package com.example.clubdeportivo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebaclubdeportivo.UserDBHelper

data class Cliente(
    val nombre: String,
    val apellido: String,
    val dni: String
)

class RegistrosActivity : AppCompatActivity() {
    private lateinit var adapter: ClientesListAdapter
    private lateinit var dbHelper: UserDBHelper
    private val clientes = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        dbHelper = UserDBHelper(this)
        val listViewRegistros = findViewById<ListView>(R.id.listViewRegistros)
        val btnVolver = findViewById<Button>(R.id.button7)

        cargarClientes()
        adapter = ClientesListAdapter(this, clientes)
        listViewRegistros.adapter = adapter

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarClientes() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "clientes",
            arrayOf("nombre", "apellido", "dni"),
            null,
            null,
            null,
            null,
            "apellido ASC, nombre ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val nombre = getString(getColumnIndexOrThrow("nombre"))
                val apellido = getString(getColumnIndexOrThrow("apellido"))
                val dni = getString(getColumnIndexOrThrow("dni"))
                clientes.add(Cliente(nombre, apellido, dni))
            }
        }
        cursor.close()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}

class ClientesListAdapter(
    private val context: AppCompatActivity,
    private val items: List<Cliente>
) : ArrayAdapter<Cliente>(context, R.layout.item_socio, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_socio, parent, false)

        val item = items[position]
        
        view.findViewById<TextView>(R.id.tvNombreApellido).text = "${item.apellido}, ${item.nombre}"
        view.findViewById<TextView>(R.id.tvDni).text = item.dni

        return view
    }
} 