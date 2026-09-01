package com.example.caluladoradeimc.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) del historial de IMC.
 *
 * Un DAO es la única puerta de entrada/salida hacia la tabla de la base de datos:
 * todas las operaciones sobre "historial_imc" pasan por aquí.
 * Room genera automáticamente la implementación de esta interfaz en tiempo de compilación
 * (por eso no hay código, solo firmas con anotaciones).
 *
 * ## Conexiones con el resto del proyecto:
 * - **No se instancia directamente**: se obtiene siempre a través de
 *   [AppDatabase.getDatabase(context).historyDao()].
 * - **Quién lo usa para escribir**: [com.example.caluladoradeimc.ui.calculator.CalculatorViewModel]
 *   llama a `insertar()` cada vez que el usuario calcula su IMC.
 * - **Quién lo usa para leer**: [com.example.caluladoradeimc.ui.history.HistoryViewModel]
 *   observa el resultado de `obtenerTodos()` para mantener actualizada la pantalla de Historial.
 */
@Dao
interface HistoryDao {

    /**
     * Es `suspend` porque las operaciones de base de datos son potencialmente lentas
     * y no deben ejecutarse en el hilo principal (bloquearían la UI)
     */
    @Insert
    suspend fun insertar(historial: HistoryEntity)

    /**
     * Devuelve un [Flow] (no una lista simple) porque Room, con Flow, **emite automáticamente
     * una nueva lista cada vez que la tabla cambia** — es decir, si el usuario calcula un
     * nuevo IMC mientras tiene el Historial abierto en otra pantalla, la lista se actualiza
     * sola, sin que nadie tenga que pedirle "refresca los datos" manualmente.
     *
     * [com.example.caluladoradeimc.ui.history.HistoryViewModel] convierte este Flow en
     * LiveData con `.asLiveData()` para que el Fragment lo pueda observar fácilmente.
     */
    @Query("SELECT * FROM historial_imc ORDER BY fechaRegistro DESC")
    fun obtenerTodos(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM historial_imc WHERE id = :id") //Elimina un único registro por su ID.
    suspend fun eliminar(id: Int)

    @Query("DELETE FROM historial_imc") //Borra todos los registros del historial de una sola vez.
    suspend fun eliminarTodo()
}