package com.example.clubdeportivo.models

data class Actividad(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val horarios: String? // Puede ser nulo si no tiene horario específico
)