package com.example.pruebaclubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDB", null, 6) {

    companion object {
        private const val TAG = "UserDBHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla usuarios (administradores del sistema)
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE,
                contrasena TEXT
            )
        """.trimIndent())

        // Cargar usuarios de prueba
        db.execSQL("INSERT INTO usuarios (nombre, contrasena) VALUES ('estefania', '1234')")
        db.execSQL("INSERT INTO usuarios (nombre, contrasena) VALUES ('vanesa', '1234')")

        // Tabla clientes (personas registradas en el sistema)
        db.execSQL("""
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                dni TEXT UNIQUE,
                apto_fisico INTEGER DEFAULT 0
            )
        """.trimIndent())

        // Tabla socios (clientes que pagaron la cuota)
        db.execSQL("""
            CREATE TABLE socios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id INTEGER,
                fecha_pago TEXT,
                fecha_vencimiento TEXT,
                estado TEXT,
                FOREIGN KEY(cliente_id) REFERENCES clientes(id)
            )
        """.trimIndent())

        // Tabla pagos de actividad diaria
        db.execSQL("""
            CREATE TABLE pagos_actividad_diaria (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id INTEGER,
                actividad TEXT,
                monto INTEGER,
                horario TEXT,
                medio_pago TEXT,
                fecha TEXT,
                FOREIGN KEY(cliente_id) REFERENCES clientes(id)
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 6) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS pagos_actividad_diaria (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER,
                    actividad TEXT,
                    monto INTEGER,
                    horario TEXT,
                    medio_pago TEXT,
                    fecha TEXT,
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                )
            """.trimIndent())
        }
        if (oldVersion < 5) {
            // Crear la nueva tabla socios
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS socios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER,
                    fecha_pago TEXT,
                    fecha_vencimiento TEXT,
                    estado TEXT,
                    FOREIGN KEY(cliente_id) REFERENCES clientes(id)
                )
            """.trimIndent())
        }
    }

    private fun tableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun login(nombre: String, contrasena: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE nombre=? AND contrasena=?",
            arrayOf(nombre, contrasena)
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    fun insertarCliente(nombre: String, apellido: String, dni: String, aptoFisico: Boolean) : Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("apto_fisico", if (aptoFisico) 1 else 0)
        }

        val resultado = db.insert("clientes", null, valores)
        return resultado != -1L
    }

    fun registrarPagoSocio(clienteId: Long): Boolean {
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaPago = Date()
        
        // Calcular fecha de vencimiento (30 días después)
        val calendar = Calendar.getInstance()
        calendar.time = fechaPago
        calendar.add(Calendar.DAY_OF_MONTH, 30)
        val fechaVencimiento = calendar.time

        // Primero actualizamos cualquier estado activo anterior a vencido
        val updateValues = ContentValues().apply {
            put("estado", "Vencido")
        }
        db.update(
            "socios",
            updateValues,
            "cliente_id = ? AND estado = 'Activo'",
            arrayOf(clienteId.toString())
        )

        // Registrar el nuevo pago
        val valores = ContentValues().apply {
            put("cliente_id", clienteId)
            put("fecha_pago", dateFormat.format(fechaPago))
            put("fecha_vencimiento", dateFormat.format(fechaVencimiento))
            put("estado", "Activo")
        }

        val resultado = db.insert("socios", null, valores)
        
        Log.d(TAG, """
            Registrando pago:
            Cliente ID: $clienteId
            Fecha Pago: ${dateFormat.format(fechaPago)}
            Fecha Vencimiento: ${dateFormat.format(fechaVencimiento)}
            Resultado: ${if (resultado != -1L) "Éxito" else "Error"}
        """.trimIndent())

        return resultado != -1L
    }

    fun obtenerClientePorDni(dni: String): Long? {
        val db = readableDatabase
        val cursor = db.query(
            "clientes",
            arrayOf("id"),
            "dni = ?",
            arrayOf(dni),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            cursor.close()
            id
        } else {
            cursor.close()
            null
        }
    }

    fun esSocioActivo(clienteId: Long): Boolean {
        val db = readableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = dateFormat.format(Date())

        val cursor = db.query(
            "socios",
            arrayOf("fecha_vencimiento"),
            "cliente_id = ? AND fecha_vencimiento >= ? AND estado = 'Activo'",
            arrayOf(clienteId.toString(), fechaActual),
            null,
            null,
            "fecha_vencimiento DESC",
            "1"
        )

        val esSocioActivo = cursor.count > 0
        cursor.close()
        
        Log.d(TAG, """
            Verificando socio activo:
            Cliente ID: $clienteId
            Fecha Actual: $fechaActual
            Es Activo: $esSocioActivo
        """.trimIndent())

        return esSocioActivo
    }

    fun actualizarEstadosSocios() {
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = dateFormat.format(Date())

        // Actualizar estados vencidos
        val valores = ContentValues().apply {
            put("estado", "Vencido")
        }

        val filasActualizadas = db.update(
            "socios",
            valores,
            "fecha_vencimiento < ? AND estado = 'Activo'",
            arrayOf(fechaActual)
        )
        
        Log.d(TAG, "Actualizados $filasActualizadas registros a estado Vencido")
    }

    fun obtenerHistorialPagosSocio(clienteId: Long): Cursor {
        val db = readableDatabase
        return db.query(
            "socios",
            arrayOf("fecha_pago", "fecha_vencimiento", "estado"),
            "cliente_id = ?",
            arrayOf(clienteId.toString()),
            null,
            null,
            "fecha_pago DESC"
        )
    }

    fun registrarPagoActividadDiaria(clienteId: Long, actividad: String, monto: Int, horario: String, medioPago: String): Boolean {
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fecha = dateFormat.format(Date())
        val valores = ContentValues().apply {
            put("cliente_id", clienteId)
            put("actividad", actividad)
            put("monto", monto)
            put("horario", horario)
            put("medio_pago", medioPago)
            put("fecha", fecha)
        }
        val resultado = db.insert("pagos_actividad_diaria", null, valores)
        return resultado != -1L
    }
}