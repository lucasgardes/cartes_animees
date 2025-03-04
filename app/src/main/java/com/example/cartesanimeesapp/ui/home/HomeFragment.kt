package com.example.cartesanimeesapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.databinding.FragmentHomeBinding
import com.example.cartesanimeesapp.models.Serie


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: SerieAdapter
    private val seriesList = listOf(
        Serie(1, "Animaux", R.drawable.serie_animaux),
        Serie(2, "Météo", R.drawable.serie_meteo),
        Serie(3, "Transport", R.drawable.serie_transport)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SerieAdapter(seriesList) { serie ->
            val action = HomeFragmentDirections.actionHomeFragmentToSerieFragment(serie.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewSeries.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSeries.adapter = adapter
    }
}
