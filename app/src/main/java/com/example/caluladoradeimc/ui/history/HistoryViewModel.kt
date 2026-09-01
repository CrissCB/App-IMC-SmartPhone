package com.example.caluladoradeimc.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.caluladoradeimc.data.AppDatabase
import com.example.caluladoradeimc.data.HistoryEntity

/**
 * A diferencia de [com.example.caluladoradeimc.ui.calculator.CalculatorViewModel],
 * este ViewModel no tiene lógica de negocio propia — no calcula nada ni valida
 * nada, solo "traduce" el [kotlinx.coroutines.flow.Flow] que expone el DAO
 * a un [LiveData], que es más cómodo de observar desde un Fragment con
 * `viewLifecycleOwner`.
 *
 * ## Conexiones con el resto del proyecto:
 * - **[com.example.caluladoradeimc.data.HistoryDao.obtenerTodos]**: fuente
 *   original de los datos. Como ese método devuelve un `Flow`, cualquier
 *   cambio en la tabla (por ejemplo, un nuevo registro insertado desde
 *   [com.example.caluladoradeimc.ui.calculator.CalculatorViewModel]) se
 *   propaga automáticamente hasta aquí, sin necesidad de refrescar manualmente.
 * - **[HistoryFragment]**: observa [historial] y se lo pasa a [HistoryAdapter]
 *   cada vez que la lista cambia.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).historyDao()

    /**
     * Lista completa de registros guardados, ordenada del más reciente al más
     * antiguo (el orden ya viene definido así desde la consulta SQL en [dao]).
     *
     * `.asLiveData()` convierte el `Flow<List<HistoryEntity>>` del DAO en un
     * `LiveData<List<HistoryEntity>>`, que es el tipo que [HistoryFragment]
     * puede observar directamente con `.observe(viewLifecycleOwner) { }`.
     */
    val historial: LiveData<List<HistoryEntity>> = dao.obtenerTodos().asLiveData()
}