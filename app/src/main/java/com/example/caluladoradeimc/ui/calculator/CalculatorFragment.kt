package com.example.caluladoradeimc.ui.calculator

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.caluladoradeimc.databinding.FragmentCalculatorBinding

/**
 * Pantalla principal de la app (destino inicial del [com.example.caluladoradeimc.R.navigation.nav_graph]).
 *
 * Responsabilidad: capturar peso y estatura ingresados por el usuario, delegar
 * todo el cálculo a [CalculatorViewModel], y reflejar en pantalla el resultado
 * o los errores que el ViewModel produzca.
 *
 * ## Conexiones con el resto del proyecto:
 * - **[CalculatorViewModel]**: fuente de toda la lógica; este Fragment solo
 *   la observa y la invoca.
 * - **`fragment_calculator.xml`**: layout inflado mediante View Binding
 *   ([FragmentCalculatorBinding]), generado automáticamente a partir del XML.
 * - **`nav_graph.xml`**: de aquí vienen las clases `CalculatorFragmentDirections`,
 *   generadas por el plugin Safe Args, que definen a qué pantallas se puede
 *   navegar desde aquí (Historial y Acerca de).
 */
class CalculatorFragment : Fragment() {

    // Patrón estándar de View Binding en Fragments: el binding es nulleable
    // y solo existe entre onCreateView() y onDestroyView(). El getter `binding`
    // evita tener que escribir `!!` cada vez que se usa en el resto de la clase.
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    // `by viewModels()` crea (o reutiliza, si ya existe) el ViewModel ligado
    // al ciclo de vida de este Fragment. Sobrevive a cambios de configuración
    // (como rotar la pantalla) sin perder el resultado calculado.
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón "Calcular": lee el texto crudo de los campos (sin validar aquí)
        // y se lo pasa tal cual al ViewModel, que es quien decide si es válido.
        binding.btnCalcular.setOnClickListener {
            val peso = binding.etPeso.text.toString()
            val estatura = binding.etEstatura.text.toString()
            viewModel.calcularImc(peso, estatura)
        }

        // Navegación hacia Historial. La acción "action_calculatorFragment_to_historyFragment"
        // está definida en nav_graph.xml; Safe Args genera este método automáticamente
        // a partir de ese id.
        binding.btnHistory.setOnClickListener {
            findNavController().navigate(
                CalculatorFragmentDirections.actionCalculatorFragmentToHistoryFragment()
            )
        }

        // Navegación hacia Acerca de, mismo mecanismo que btnHistory.
        binding.btnAbout.setOnClickListener {
            findNavController().navigate(
                CalculatorFragmentDirections.actionCalculatorFragmentToAboutFragment()
            )
        }

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        binding.switchTheme.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        observarViewModel()
    }

    /**
     * Suscribe la UI a los dos LiveData que expone [CalculatorViewModel].
     *
     * `viewLifecycleOwner` (y no `this`) se usa deliberadamente como dueño de
     * la observación: así, si el Fragment queda en el back stack pero su vista
     * fue destruida (por ejemplo, al navegar a Historial), las actualizaciones
     * dejan de llegar a una vista que ya no existe, evitando crashes.
     */
    private fun observarViewModel() {
        viewModel.resultado.observe(viewLifecycleOwner) { resultado ->
            if (resultado != null) {
                binding.tvResultadoImc.text = String.format("%.1f", resultado.imc)
                binding.tvCategoria.text = resultado.categoria
                binding.ivResultadoCategoria.setImageResource(resultado.imagenResId)
                binding.ivResultadoCategoria.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje != null) {
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Libera la referencia al binding cuando la vista del Fragment se destruye.
     *
     * Es obligatorio en Fragments (a diferencia de Activities): la vista del
     * Fragment puede destruirse antes que el Fragment mismo (por ejemplo, al
     * navegar a otra pantalla pero manteniéndose en el back stack), y si no se
     * libera el binding aquí, se produce una fuga de memoria porque el binding
     * retiene referencias a vistas que ya no existen.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}