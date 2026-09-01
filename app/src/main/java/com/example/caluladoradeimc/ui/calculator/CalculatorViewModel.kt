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
    val categoria: String,
    val imagenResId: Int
)

/**
 * Hereda de [AndroidViewModel] (en vez de [androidx.lifecycle.ViewModel] puro),
 * tiene acceso al `Application` context, necesario para: (1) obtener la instancia
 * de la base de datos vía [AppDatabase.getDatabase], y (2) leer strings de
 * `strings.xml` para los mensajes de error.
 *
 * ## Conexiones con el resto del proyecto:
 * - **[com.example.caluladoradeimc.ui.calculator.CalculatorFragment]** lo obtiene
 *   con `by viewModels()`, llama a [calcularImc] cuando el usuario pulsa "Calcular",
 *   y observa [resultado] y [error] para actualizar la pantalla.
 * - **[com.example.caluladoradeimc.data.AppDatabase] / [com.example.caluladoradeimc.data.HistoryDao]**:
 *   este ViewModel es quien inicia el guardado de cada cálculo en la base de datos.
 *   No hay ningún otro punto de la app donde se inserten registros nuevos.
 */
class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    // Acceso al DAO, obtenido una sola vez al crear el ViewModel.
    private val dao = AppDatabase.getDatabase(application).historyDao()

    // Backing property pattern: _resultado es mutable y privado (solo este ViewModel
    // puede modificarlo)
    private val _resultado = MutableLiveData<ResultadoImc?>()
    val resultado: LiveData<ResultadoImc?> = _resultado

    // Mensaje de error a mostrar (ej. en un Toast) cuando la validación falla.
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Punto de entrada principal: valida los datos ingresados, calcula el IMC,
     * determina la categoría e imagen correspondientes, y dispara el guardado
     * automático en el historial.
     *
     * Se llama directamente desde el listener del botón "Calcular" en
     * [CalculatorFragment], pasándole el texto crudo de los `EditText` tal cual
     * lo escribió el usuario (sin convertir ni validar antes).
     *
     * @param pesoTexto texto ingresado en el campo de peso (ej. "70.5" o "70,5")
     * @param estaturaTexto texto ingresado en el campo de estatura (ej. "1.75")
     */
    fun calcularImc(pesoTexto: String, estaturaTexto: String) {
        // toFloatOrNull() solo reconoce el punto.
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

        // Limpia cualquier error previo antes de mostrar un resultado válido.
        _error.value = null

        // Fórmula estándar del IMC: peso (kg) / estatura² (m).
        val imc = peso / (estatura * estatura)
        val categoria = obtenerCategoria(imc)
        val imagen = obtenerImagenCategoria(categoria)

        _resultado.value = ResultadoImc(imc, categoria, imagen)

        guardarEnHistorial(peso, estatura, imc, categoria)
    }

    /**
     * Clasifica un valor de IMC en una categoría textual, según los rangos
     * estándar de la OMS (Organización Mundial de la Salud).
     */
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

    // Determina qué imagen ilustrativa mostrar en pantalla según la categoría.
    private fun obtenerImagenCategoria(categoria: String): Int {
        return when (categoria) {
            "Delgadez" -> R.drawable.img_delgadez
            "Peso Normal" -> R.drawable.img_normal
            "Sobrepeso" -> R.drawable.img_sobrepeso
            else -> R.drawable.img_obesidad // Agrupa Grado I, II y III
        }
    }

    /**
     * Se ejecuta dentro de `viewModelScope.launch`, lo que significa que corre
     * en una corrutina ligada al ciclo de vida del ViewModel: si el ViewModel
     * se destruye antes de que termine el guardado, la corrutina se cancela
     * automáticamente (evitando fugas de memoria o escrituras huérfanas).
     */
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