package com.example.clubdeportivo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivo.models.Vencimiento

class RegistrosActivity : AppCompatActivity() {
    private lateinit var adapter: VencimientosListAdapter
    private val vencimientos = mutableListOf(
        Vencimiento(1, "33520145", "1/4/25", "Vencido"),
        Vencimiento(2, "16548852", "25/4/25", "Pagado"),
        Vencimiento(3, "15428756", "30/5/25", "Pagado"),
        Vencimiento(4, "32564856", "2/5/25", "Pagado"),
        Vencimiento(5, "20365123", "3/6/25", "Pagado"),
        Vencimiento(6, "20548632", "1/5/25", "Vencido")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        val listViewRegistros = findViewById<ListView>(R.id.listViewRegistros)
        val btnVolver = findViewById<Button>(R.id.button7)
        val btnBorrar = findViewById<Button>(R.id.btnBorrar)
        val btnCargarMas = findViewById<Button>(R.id.btnCargarMas)

        adapter = VencimientosListAdapter(this, vencimientos)
        listViewRegistros.adapter = adapter

        btnBorrar.setOnClickListener {
            vencimientos.clear()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Registros borrados", Toast.LENGTH_SHORT).show()
        }

        btnCargarMas.setOnClickListener {
            // Simulamos cargar más registros
            vencimientos.addAll(vencimientos.take(3))
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Registros cargados", Toast.LENGTH_SHORT).show()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}

class VencimientosListAdapter(
    private val context: AppCompatActivity,
    private val items: MutableList<Vencimiento>
) : ArrayAdapter<Vencimiento>(context, R.layout.item_vencimiento, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_vencimiento, parent, false)

        val item = items[position]
        
        view.findViewById<TextView>(R.id.tvId).text = item.id.toString()
        view.findViewById<TextView>(R.id.tvDni).text = item.dni
        view.findViewById<TextView>(R.id.tvFecha).text = item.fecha
        
        val tvEstado = view.findViewById<TextView>(R.id.tvEstado)
        tvEstado.text = item.estado
        tvEstado.setTextColor(if (item.estado == "Vencido") Color.RED else Color.GREEN)

        return view
    }
} 