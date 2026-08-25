package com.example.caluladoradeimc.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caluladoradeimc.data.HistoryEntity
import com.example.caluladoradeimc.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val formatoFecha = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))

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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}