package com.example.caluladoradeimc.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert
    suspend fun insertar(historial: HistoryEntity)

    @Query("SELECT * FROM historial_imc ORDER BY fechaRegistro DESC")
    fun obtenerTodos(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM historial_imc WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("DELETE FROM historial_imc")
    suspend fun eliminarTodo()
}