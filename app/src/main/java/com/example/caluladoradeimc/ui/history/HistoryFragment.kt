package com.example.caluladoradeimc.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caluladoradeimc.databinding.FragmentHistoryBinding

/**
 * Pantalla de Historial: muestra todos los cálculos de IMC guardados,
 * del más reciente al más antiguo, o un mensaje si aún no hay ninguno.
 *
 * Al igual que [com.example.caluladoradeimc.ui.calculator.CalculatorFragment],
 * este Fragment no tiene lógica propia — observa [HistoryViewModel.historial]
 * y delega el trabajo de pintar la lista al [HistoryAdapter].
 *
 * ## Conexiones con el resto del proyecto:
 * - **[HistoryViewModel]**: fuente de la lista observada.
 * - **[HistoryAdapter]**: se crea una sola vez en [configurarRecyclerView] y
 *   recibe listas nuevas cada vez que el ViewModel emite un cambio.
 * - **`fragment_history.xml`**: layout inflado vía View Binding.
 * - **`nav_graph.xml`**: este Fragment es un destino registrado en el grafo,
 *   necesario para que `findNavController().popBackStack()` sepa a qué
 *   pantalla regresar (Calculadora, ya que es desde donde se navegó aquí).
 */
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()

    // `lateinit` porque el Adapter no puede crearse en la declaración de la
    // propiedad (necesita existir la vista primero); se inicializa en
    // configurarRecyclerView(), llamado desde onViewCreated().
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()

        // "Regresar" usa popBackStack() en vez de navegar explícitamente a
        // CalculatorFragment: esto reutiliza la instancia que ya existía en
        // el back stack (con su estado intacto) en vez de crear una nueva.
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Cada vez que la lista cambia (nuevo registro guardado, o carga
        // inicial), se actualiza el Adapter y se decide si mostrar la lista
        // o el mensaje de "historial vacío".
        viewModel.historial.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            binding.tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHistorial.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    /**
     * Prepara el RecyclerView: crea el Adapter y le asigna un LinearLayoutManager
     * (lista vertical simple, un elemento por fila, sin necesidad de scroll
     * horizontal ni grilla).
     */
    private fun configurarRecyclerView() {
        adapter = HistoryAdapter()
        binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistorial.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}