package com.example.caluladoradeimc.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.caluladoradeimc.databinding.FragmentAboutBinding

/**
 * Pantalla estática "Acerca de": muestra información fija sobre la app
 * (nombre, versión, descripción, desarrollador) y permite regresar a
 * la pantalla anterior.
 *
 * Es la pantalla más simple de toda la app: a diferencia de
 * [com.example.caluladoradeimc.ui.calculator.CalculatorFragment] y
 * [com.example.caluladoradeimc.ui.history.HistoryFragment], no tiene
 * ViewModel asociado, porque no hay ningún dato dinámico que calcular,
 * validar u observar — todo el contenido viene directamente de
 * `strings.xml` y se define en el layout XML, no en tiempo de ejecución.
 *
 * ## Conexiones con el resto del proyecto:
 * - **`fragment_about.xml`**: layout inflado vía View Binding
 *   ([FragmentAboutBinding]); contiene todo el texto ya resuelto con
 *   referencias a `strings.xml` (`nombre_app`, `version_app`,
 *   `descripcion_app`, `desarrollado_por`), por lo que este Fragment
 *   no necesita asignar texto manualmente en Kotlin.
 * - **`nav_graph.xml`**: destino registrado en el grafo; se llega aquí
 *   solo desde [com.example.caluladoradeimc.ui.calculator.CalculatorFragment]
 *   (botón `btnAbout`), y se regresa con `popBackStack()`, igual que en
 *   [com.example.caluladoradeimc.ui.history.HistoryFragment].
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Única interacción de esta pantalla: regresar a la pantalla anterior
        // (siempre CalculatorFragment, ya que es el único punto de entrada).
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}