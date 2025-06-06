package com.example.pruebaclubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDB", null, 2) {

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
            CREATE TABLE socios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                dni TEXT UNIQUE
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
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

    //Función que va al MenuActivity
    fun insertarSocio(nombre: String, dni: String) : Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("dni", dni)
        }

        val resultado = db.insert("socios", null, valores)
        return resultado != -1L
    }
}