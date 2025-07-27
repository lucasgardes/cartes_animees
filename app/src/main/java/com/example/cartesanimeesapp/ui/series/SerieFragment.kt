package com.example.cartesanimeesapp.ui.series

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.databinding.FragmentSerieBinding
import com.example.cartesanimeesapp.models.Serie
import com.example.cartesanimeesapp.network.Constants
import com.example.cartesanimeesapp.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.OkHttpClient
import okhttp3.Request

import java.io.IOException

private var currentSessionId: Int? = null

class SerieFragment : Fragment() {

    private var _binding: FragmentSerieBinding? = null
    private val binding get() = _binding!!

    private var currentIndex = 0
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var serie: Serie
    private var patientId: Int = -1

    private var isCartoon = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSerieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSwitchStyle.isChecked = false
        val serieId = arguments?.getInt("serieId")

        if (serieId == null) {
            Log.e("SerieFragment", "🚨 ERREUR : serieId est NULL !")
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        patientId = sharedPreferences.getInt("patient_id", -1)

        if (patientId == -1) {
            Log.e("SerieFragment", "❌ patient_id introuvable, impossible de démarrer la session")
            return
        }

        Log.d("SerieFragment", "✅ serieId reçu : $serieId")
        getSerieFromApi(serieId)

        binding.btnNext.setOnClickListener {
            if (::serie.isInitialized && currentIndex < serie.elements.size - 1) {
                currentIndex++
                loadSerieElement()
            }
        }

        binding.btnPrev.setOnClickListener {
            if (::serie.isInitialized && currentIndex > 0) {
                currentIndex--
                loadSerieElement()
            }
        }

        binding.btnReplaySound.setOnClickListener {
            playAudio(serie.elements[currentIndex].son_path)
        }

        binding.btnReturnHome.setOnClickListener {
            findNavController().navigate(R.id.action_serieFragment_to_homeFragment)
        }

        binding.btnSwitchStyle.setOnCheckedChangeListener { _, _ ->
            isCartoon = !isCartoon
            loadSerieElement()
        }
    }

    private fun getSerieFromApi(serieId: Int) {
        RetrofitClient.apiService.getSerieById(id = serieId, token = Constants.API_TOKEN).enqueue(object : Callback<Serie> {
            override fun onResponse(call: Call<Serie>, response: Response<Serie>) {
                if (response.isSuccessful && response.body() != null) {
                    serie = response.body()!!
                    loadSerieElement()

                    RetrofitClient.apiService.startSerieSession(
                        action = "start_session",
                        patientId = patientId,
                        serieId = serie.id,
                        token = Constants.API_TOKEN
                    ).enqueue(object : Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            if (response.isSuccessful) {
                                currentSessionId = response.body()
                                Log.d("API", "Session démarrée avec ID = $currentSessionId")
                            } else {
                                Log.e("API", "Erreur serveur: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Log.e("API", "Erreur réseau: ${t.message}")
                        }
                    })
                } else {
                    Log.e("API", "Erreur de réponse : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Serie>, t: Throwable) {
                Log.e("API", "Échec de la requête", t)
            }
        })
    }

    private fun loadSerieElement() {
        val currentElement = serie.elements[currentIndex]
        val imageToShow = if (isCartoon) currentElement.image_cartoon else currentElement.image_real
        Glide.with(this)
            .asGif()
            .load(imageToShow)
            .placeholder(R.drawable.placeholder)
            .into(binding.imageSerie)

        binding.imageSerie.setOnClickListener {
            playAudio(serie.elements[currentIndex].son_path)
        }

        updateProgressText()
    }

    private fun playAudio(audioUrl: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepare()
                start()
            }

            RetrofitClient.apiService.replaySound(
                patientId = patientId, // Remplacer avec le vrai ID du patient
                animationId = serie.elements[currentIndex].id,
                token = Constants.API_TOKEN
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("API", "Relecture enregistrée")
                    } else {
                        Log.e("API", "Erreur relecture: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("API", "Erreur réseau relecture: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Erreur lors de la lecture audio", e)
        }
    }

    private fun updateProgressText() {
        val position = currentIndex + 1
        val total = serie.elements.size
        binding.textProgress.text = "$position / $total"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer?.release()
        mediaPlayer = null

        currentSessionId?.let { sessionId ->
            RetrofitClient.apiService.endSerieSession(
                action = "end_session",
                sessionId = sessionId,
                token = Constants.API_TOKEN
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("API", "Fin de session enregistrée")
                    } else {
                        Log.e("API", "Erreur de fin de session: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("API", "Erreur réseau fin de session: ${t.message}")
                }
            })
        }
    }
}
