package com.example.cartesanimeesapp.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.databinding.FragmentHomeBinding
import com.example.cartesanimeesapp.models.Serie
import com.example.cartesanimeesapp.network.Constants
import com.example.cartesanimeesapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: SerieAdapter
    private var seriesList = listOf<Serie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        binding.recyclerViewSeries.layoutManager = LinearLayoutManager(requireContext())

        loadSeriesFromApi()
    }

    private fun loadSeriesFromApi() {
        val sharedPreferences = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val patientId = sharedPreferences.getInt("patient_id", -1)

        if (patientId == -1) {
            Log.e("HomeFragment", "❌ patient_id manquant, utilisateur non connecté.")
            return
        }
        RetrofitClient.apiService.getSeries(patientId = patientId, token = Constants.API_TOKEN).enqueue(object : Callback<List<Serie>> {
            override fun onResponse(call: Call<List<Serie>>, response: Response<List<Serie>>) {
                if (response.isSuccessful && response.body() != null) {
                    seriesList = response.body()!!
                    Log.d("HomeFragment", "✅ Séries reçues : ${seriesList.size}")
                    Log.d("HomeFragment", "🔍 Contenu JSON : $seriesList")
                    adapter = SerieAdapter(seriesList) { serie ->
                        val action = HomeFragmentDirections.actionHomeFragmentToSerieFragment(serie.id)
                        findNavController().navigate(action)
                    }
                    binding.recyclerViewSeries.adapter = adapter
                } else {
                    Log.e("HomeFragment", "Erreur de réponse API : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Serie>>, t: Throwable) {
                Log.e("HomeFragment", "Échec appel API : ${t.message}")
            }
        })
    }
}
