package com.example.caluladoradeimc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_imc")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val peso: Float,          // en kilogramos

    val estatura: Float,      // en metros (ej. 1.70)

    val imc: Float,           // resultado del cálculo IMC

    val resultado: String,    // categoría: "Delgadez", "Normal", "Sobrepeso", "Obesidad"

    val fechaRegistro: Long   // timestamp (System.currentTimeMillis())
)