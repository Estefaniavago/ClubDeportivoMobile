package com.example.clubdeportivo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivo.models.Vencimiento

class ListarVencimientoActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VencimientosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_vencimiento)

        val btnVolver = findViewById<Button>(R.id.button7)
        val btnBorrar = findViewById<Button>(R.id.btnBorrar)
        val btnCargarMas = findViewById<Button>(R.id.btnCargarMas)
        recyclerView = findViewById(R.id.recyclerVencimientos)

        // Datos de ejemplo
        val vencimientos = listOf(
            Vencimiento(1, "33520145", "1/4/25", "Vencido"),
            Vencimiento(2, "16548852", "25/4/25", "Pagado"),
            Vencimiento(3, "15428756", "30/5/25", "Pagado"),
            Vencimiento(4, "32564856", "2/5/25", "Pagado"),
            Vencimiento(5, "20365123", "3/6/25", "Pagado"),
            Vencimiento(6, "20548632", "1/5/25", "Vencido")
        )

        adapter = VencimientosAdapter(vencimientos.toMutableList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnVolver.setOnClickListener {
            finish()
        }

        btnBorrar.setOnClickListener {
            adapter.clearItems()
        }

        btnCargarMas.setOnClickListener {
            // Simular carga de más datos
            adapter.addItems(vencimientos)
        }
    }
}

class VencimientosAdapter(private val items: MutableList<Vencimiento>) : 
    RecyclerView.Adapter<VencimientosAdapter.VencimientoViewHolder>() {

    class VencimientoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvId)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
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
        holder.tvId.text = item.id.toString()
        holder.tvDni.text = item.dni
        holder.tvFecha.text = item.fecha
        holder.tvEstado.text = item.estado
        
        if (item.estado == "Vencido") {
            holder.tvEstado.setTextColor(Color.RED)
        } else {
            holder.tvEstado.setTextColor(Color.GREEN)
        }
    }

    override fun getItemCount() = items.size

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<Vencimiento>) {
        val startPos = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPos, newItems.size)
    }
} 