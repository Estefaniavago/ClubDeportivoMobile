package com.example.clubdeportivo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivo.models.Vencimiento
import com.example.pruebaclubdeportivo.UserDBHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListarVencimientoActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VencimientosAdapter
    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_vencimiento)

        dbHelper = UserDBHelper(this)
        val btnVolver = findViewById<Button>(R.id.button7)
        recyclerView = findViewById(R.id.recyclerVencimientos)

        // Cargar vencimientos desde la base de datos
        val vencimientos = cargarVencimientos()
        
        if (vencimientos.isEmpty()) {
            Toast.makeText(this, "No hay pagos registrados", Toast.LENGTH_LONG).show()
        }

        adapter = VencimientosAdapter(vencimientos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarVencimientos(): List<Vencimiento> {
        val vencimientos = mutableListOf<Vencimiento>()
        val db = dbHelper.readableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = dateFormat.format(Date())

        // Consulta para obtener los datos de socios y clientes
        val query = """
            SELECT s.id, c.dni, c.nombre, c.apellido, s.fecha_vencimiento, s.fecha_pago,
                   CASE 
                       WHEN s.fecha_vencimiento >= ? THEN 'Pagado'
                       ELSE 'Vencido'
                   END as estado
            FROM socios s
            INNER JOIN clientes c ON s.cliente_id = c.id
            ORDER BY s.fecha_pago DESC
        """.trimIndent()

        Log.d("ListarVencimiento", "Fecha actual: $fechaActual")
        Log.d("ListarVencimiento", "Ejecutando query: $query")

        val cursor = db.rawQuery(query, arrayOf(fechaActual))

        Log.d("ListarVencimiento", "Número de registros encontrados: ${cursor.count}")

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("id"))
                val dni = it.getString(it.getColumnIndexOrThrow("dni"))
                val nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
                val apellido = it.getString(it.getColumnIndexOrThrow("apellido"))
                val fechaVencimiento = it.getString(it.getColumnIndexOrThrow("fecha_vencimiento"))
                val fechaPago = it.getString(it.getColumnIndexOrThrow("fecha_pago"))
                val estado = it.getString(it.getColumnIndexOrThrow("estado"))

                Log.d("ListarVencimiento", """
                    Registro encontrado:
                    ID: $id
                    DNI: $dni
                    Nombre: $nombre
                    Apellido: $apellido
                    Fecha Vencimiento: $fechaVencimiento
                    Fecha Pago: $fechaPago
                    Estado: $estado
                """.trimIndent())

                // Formatear la fecha para mostrarla
                val fechaDb = dateFormat.parse(fechaVencimiento)
                val fechaMostrar = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(fechaDb!!)

                vencimientos.add(Vencimiento(id, dni, nombre, apellido, fechaMostrar, estado))
            }
        }

        return vencimientos
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la lista cada vez que la actividad vuelve a primer plano
        val vencimientos = cargarVencimientos()
        adapter = VencimientosAdapter(vencimientos)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}

class VencimientosAdapter(private val items: List<Vencimiento>) : 
    RecyclerView.Adapter<VencimientosAdapter.VencimientoViewHolder>() {

    class VencimientoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvNombreApellido: TextView = view.findViewById(R.id.tvNombreApellido)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VencimientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vencimiento, parent, false)
        return VencimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VencimientoViewHolder, position: Int) {
        val item = items[position]
        holder.tvDni.text = item.dni
        holder.tvNombreApellido.text = "${item.apellido}, ${item.nombre}"
        holder.tvFecha.text = item.fechaVencimiento
        holder.tvEstado.text = item.estado
        
        if (item.estado == "Vencido") {
            holder.tvEstado.setTextColor(Color.RED)
        } else {
            holder.tvEstado.setTextColor(Color.GREEN)
        }
    }

    override fun getItemCount() = items.size
} 