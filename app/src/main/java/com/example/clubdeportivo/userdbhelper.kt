package com.example.pruebaclubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDB", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE,
                contrasena TEXT
            )
        """.trimIndent())

        // Cargar un usuario de prueba
        db.execSQL("INSERT INTO usuarios (nombre, contrasena) VALUES ('estefania', '1234')")
        db.execSQL("INSERT INTO usuarios (nombre, contrasena) VALUES ('vanesa', '1234')")

        db.execSQL("""
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                dni TEXT UNIQUE,
                apto_fisico INTEGER DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            // Respaldar datos de la tabla socios si existe
            if (tableExists(db, "socios")) {
                db.execSQL("""
                    CREATE TABLE clientes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT,
                        apellido TEXT,
                        dni TEXT UNIQUE,
                        apto_fisico INTEGER DEFAULT 0
                    )
                """.trimIndent())
                
                // Copiar datos de socios a clientes
                db.execSQL("""
                    INSERT INTO clientes (nombre, apellido, dni, apto_fisico)
                    SELECT nombre, apellido, dni, apto_fisico FROM socios
                """.trimIndent())
                
                // Eliminar tabla socios
                db.execSQL("DROP TABLE socios")
            } else {
                // Si la tabla socios no existe, crear clientes desde cero
                db.execSQL("""
                    CREATE TABLE clientes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT,
                        apellido TEXT,
                        dni TEXT UNIQUE,
                        apto_fisico INTEGER DEFAULT 0
                    )
                """.trimIndent())
            }
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
}