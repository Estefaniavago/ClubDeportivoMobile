package com.example.clubdeportivo.models

data class Vencimiento(
    val id: Int,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val fechaVencimiento: String,
    val estado: String
) 