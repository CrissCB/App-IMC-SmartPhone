package com.example.caluladoradeimc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ## Conexiones con el resto del proyecto:
 * - **Quién la crea**: [com.example.caluladoradeimc.ui.calculator.CalculatorViewModel] construye
 *   una instancia de esta clase cada vez que el usuario calcula su IMC, y la envía a [HistoryDao]
 *   para guardarla.
 * - **Quién la lee**: [HistoryDao] la devuelve en forma de lista (`Flow<List<HistoryEntity>>`),
 *   que [com.example.caluladoradeimc.ui.history.HistoryViewModel] observa y expone al Fragment,
 *   y que [com.example.caluladoradeimc.ui.history.HistoryAdapter] recibe fila por fila para
 *   pintar cada card del historial.
 *
 * En resumen: es el "objeto de datos" que viaja desde la Calculadora hacia la base de datos,
 * y desde la base de datos hacia la pantalla de Historial.
 */
@Entity(tableName = "historial_imc")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val peso: Float,          // en kilogramos

    val estatura: Float,      // en metros (ej. 1.70)

    val imc: Float,           // resultado del cálculo IMC

    val resultado: String,    // categoría: "Delgadez", "Normal", "Sobrepeso", "Obesidad"

    val fechaRegistro: Long   // timestamp (System.currentTimeMillis()) guardado en milisegundos
)