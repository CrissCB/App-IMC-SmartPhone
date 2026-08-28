package com.example.caluladoradeimc.ui.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.caluladoradeimc.R
import com.example.caluladoradeimc.data.AppDatabase
import com.example.caluladoradeimc.data.HistoryEntity
import kotlinx.coroutines.launch

// Clase auxiliar para agrupar el resultado que se muestra en pantalla
data class ResultadoImc(
    val imc: Float,
    val categoria: String
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).historyDao()

    private val _resultado = MutableLiveData<ResultadoImc?>()
    val resultado: LiveData<ResultadoImc?> = _resultado

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun calcularImc(pesoTexto: String, estaturaTexto: String) {
        val peso = pesoTexto.replace(",", ".").toFloatOrNull()
        val estatura = estaturaTexto.replace(",", ".").toFloatOrNull()
        val contexto = getApplication<Application>()

        // Validaciones básicas
        if (peso == null || estatura == null) {
            _error.value = contexto.getString(R.string.error_valores_invalidos)
            return
        }
        if (peso <= 0f || estatura <= 0f) {
            _error.value = contexto.getString(R.string.error_valores_negativos)
            return
        }

        _error.value = null

        val imc = peso / (estatura * estatura)
        val categoria = obtenerCategoria(imc)

        _resultado.value = ResultadoImc(imc, categoria)

        guardarEnHistorial(peso, estatura, imc, categoria)
    }

    private fun obtenerCategoria(imc: Float): String {
        return when {
            imc < 18.5f -> "Delgadez"
            imc < 25f -> "Peso Normal"
            imc < 30f -> "Sobrepeso"
            imc < 35f -> "Obesidad Grado I"
            imc < 40f -> "Obesidad Grado II"
            else -> "Obesidad Grado III"
        }
    }

    private fun guardarEnHistorial(peso: Float, estatura: Float, imc: Float, categoria: String) {
        viewModelScope.launch {
            val registro = HistoryEntity(
                peso = peso,
                estatura = estatura,
                imc = imc,
                resultado = categoria,
                fechaRegistro = System.currentTimeMillis()
            )
            dao.insertar(registro)
        }
    }
}