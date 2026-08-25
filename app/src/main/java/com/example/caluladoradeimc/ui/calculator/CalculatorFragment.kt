package com.example.caluladoradeimc.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.caluladoradeimc.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

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

        binding.btnCalcular.setOnClickListener {
            val peso = binding.etPeso.text.toString()
            val estatura = binding.etEstatura.text.toString()
            viewModel.calcularImc(peso, estatura)
        }

        binding.btnHistory.setOnClickListener {
            findNavController().navigate(
                CalculatorFragmentDirections.actionCalculatorFragmentToHistoryFragment()
            )
        }

        binding.btnAbout.setOnClickListener {
            findNavController().navigate(
                CalculatorFragmentDirections.actionCalculatorFragmentToAboutFragment()
            )
        }

        observarViewModel()
    }

    private fun observarViewModel() {
        viewModel.resultado.observe(viewLifecycleOwner) { resultado ->
            if (resultado != null) {
                binding.tvResultadoImc.text = String.format("%.1f", resultado.imc)
                binding.tvCategoria.text = resultado.categoria
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje != null) {
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}