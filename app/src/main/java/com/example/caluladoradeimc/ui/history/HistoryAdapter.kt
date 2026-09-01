package com.example.caluladoradeimc.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caluladoradeimc.R
import com.example.caluladoradeimc.data.HistoryEntity
import com.example.caluladoradeimc.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter del RecyclerView que muestra la lista de registros en la pantalla
 * de Historial. Es el puente entre los datos crudos ([HistoryEntity]) y lo
 * que efectivamente se pinta en cada fila (`item_history.xml`).
 *
 * Hereda de [ListAdapter] (en vez de [RecyclerView.Adapter] puro), lo que
 * delega en un [DiffCallback] el trabajo de comparar listas viejas y nuevas:
 * cuando [HistoryFragment] llama a `submitList()`, el Adapter calcula solo
 * las diferencias y anima únicamente las filas que cambiaron, en vez de
 * redibujar la lista completa cada vez.
 *
 * ## Conexiones con el resto del proyecto:
 * - **[HistoryFragment]**: lo crea, lo asigna al `RecyclerView`, y le entrega
 *   listas nuevas vía `submitList()` cada vez que [HistoryViewModel.historial] cambia.
 * - **`item_history.xml`**: layout de cada fila, inflado vía View Binding
 *   ([ItemHistoryBinding]).
 * - **`colors.xml`**: usa los colores semánticos (`imc_delgadez`, `imc_normal`,
 *   `imc_sobrepeso`, `imc_obesidad`) para diferenciar visualmente cada categoría.
 * - **Duplica lógica con** [com.example.caluladoradeimc.ui.calculator.CalculatorViewModel.obtenerImagenCategoria]:
 *   ambos agrupan las 3 obesidades en una sola categoría visual. Si cambia el
 *   umbral o la agrupación en un lado, hay que replicarlo en el otro (ver nota
 *   pendiente sobre extraer esto a una clase compartida `ImcHelper`).
 */
class HistoryAdapter : ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    /**
     * Representa y gestiona una única fila visible en pantalla.
     *
     * RecyclerView reutiliza un número limitado de ViewHolders (solo los que
     * caben en pantalla + un margen), en vez de crear una vista por cada
     * registro del historial — así la lista sigue siendo fluida aunque haya
     * cientos de registros guardados.
     */
    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Formato de fecha fijo en español ("25 ago 2026, 10:30"), aplicado
        // sobre el timestamp crudo (Long) que se guarda en HistoryEntity.fechaRegistro.
        private val formatoFecha = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))

        /**
         * Vuelca los datos de un [HistoryEntity] sobre las vistas de esta fila.
         * Se llama una vez por cada posición visible, cada vez que el
         * RecyclerView necesita mostrar o actualizar esa fila.
         */
        fun bind(item: HistoryEntity) {
            binding.tvFecha.text = formatoFecha.format(item.fechaRegistro)
            binding.tvCategoria.text = item.resultado
            binding.tvImcValor.text = String.format(Locale.getDefault(), "%.1f", item.imc)
            binding.tvPesoEstatura.text = String.format(
                Locale.getDefault(),
                "%.1f kg · %.2f m",
                item.peso,
                item.estatura
            )

            // Aplica el color semántico tanto a la franja lateral (vColorCategoria)
            // como al texto de la categoría, reforzando la señal visual sin
            // depender de una imagen (a diferencia de la pantalla Calculadora).
            val colorRes = obtenerColorCategoria(item.resultado)
            val color = ContextCompat.getColor(binding.root.context, colorRes)
            binding.vColorCategoria.setBackgroundColor(color)
            binding.tvCategoria.setTextColor(color)
        }

        /**
         * Mapea el texto de la categoría (guardado tal cual en la base de datos)
         * a su color correspondiente en `colors.xml`.
         *
         * Igual que en CalculatorViewModel, las 3 categorías de Obesidad
         * comparten un mismo color (rojo), ya que la diferencia entre grados
         * es de severidad clínica, no algo que se necesite distinguir a
         * simple vista en una lista.
         */
        private fun obtenerColorCategoria(categoria: String): Int {
            return when (categoria) {
                "Delgadez" -> R.color.imc_delgadez
                "Peso Normal" -> R.color.imc_normal
                "Sobrepeso" -> R.color.imc_sobrepeso
                else -> R.color.imc_obesidad // Agrupa Grado I, II y III
            }
        }
    }

    /**
     * Crea un ViewHolder nuevo inflando `item_history.xml`.
     * Se llama solo cuando RecyclerView necesita una vista nueva (no una reciclada).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    /**
     * Conecta un ViewHolder (nuevo o reciclado) con el dato que le corresponde
     * según su posición en la lista actual.
     */
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Le indica a [ListAdapter] cómo comparar dos listas para saber qué
     * cambió (qué se agregó, qué se quitó, qué se modificó), y así animar
     * solo esas diferencias en vez de redibujar todo.
     */
    private class DiffCallback : DiffUtil.ItemCallback<HistoryEntity>() {
        // Dos items son "el mismo" si tienen el mismo id, aunque sus otros campos difieran.
        override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * Dos items con el mismo id tienen además el mismo contenido si todos
         * sus campos coinciden (comparación automática, ya que `HistoryEntity`
         * es un `data class`).
         */
        override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}