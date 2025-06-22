package com.example.clubdeportivo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivo.UserDBHelper
import com.example.clubdeportivo.models.Cliente


class RegistrosActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper
    private lateinit var clientesAdapter: ClientesRecyclerAdapter // Cambiamos el tipo de adaptador
    private val clientes = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        dbHelper = UserDBHelper(this)

        // Referencia al nuevo RecyclerView
        val recyclerViewRegistros = findViewById<RecyclerView>(R.id.recyclerViewRegistros)
        val btnVolver = findViewById<Button>(R.id.button7)

        recyclerViewRegistros.layoutManager = LinearLayoutManager(this)
        cargarClientes()
        clientesAdapter = ClientesRecyclerAdapter(clientes)
        recyclerViewRegistros.adapter = clientesAdapter

        mostrarNombreUsuarioEnEncabezado()

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarClientes() {

        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "clientes",
            arrayOf("id", "nombre", "apellido", "dni"),
            null, null, null, null,
            "apellido ASC, nombre ASC"
        )


        clientes.clear()

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
                val apellido = it.getString(it.getColumnIndexOrThrow("apellido"))
                val dni = it.getString(it.getColumnIndexOrThrow("dni"))
                clientes.add(Cliente(id, nombre, apellido, dni))
            }
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
    private fun mostrarNombreUsuarioEnEncabezado() {
        // 1. Abrimos las mismas SharedPreferences
        val sharedPrefs = getSharedPreferences("ClubDeportivoPrefs", Context.MODE_PRIVATE)

        // 2. Leemos el nombre guardado. Si no encontramos nada, usamos "Usuario" como valor por defecto.
        val nombreUsuario = sharedPrefs.getString("NOMBRE_USUARIO_LOGUEADO", "Usuario")

        // 3. Buscamos el TextView del encabezado y le ponemos el nombre
        // El ?.text es un "safe call", evita que la app crashee si por alguna razón el TextView no existiera.
        val tvUsuario = findViewById<TextView>(R.id.textView4)
        tvUsuario?.text = nombreUsuario
    }

}

class ClientesRecyclerAdapter(private val items: List<Cliente>) :
    RecyclerView.Adapter<ClientesRecyclerAdapter.ClienteViewHolder>() {


    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreApellido: TextView = view.findViewById(R.id.tvNombreApellido)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return ClienteViewHolder(view)
    }


    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val item = items[position]
        holder.tvNombreApellido.text = "${item.apellido}, ${item.nombre}"
        holder.tvDni.text = item.dni
    }

    override fun getItemCount() = items.size
}