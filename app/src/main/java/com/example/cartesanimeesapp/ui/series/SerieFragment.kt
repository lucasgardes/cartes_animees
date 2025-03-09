package com.example.cartesanimeesapp.ui.series

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cartesanimeesapp.databinding.FragmentSerieBinding
import com.example.cartesanimeesapp.models.Serie
import com.bumptech.glide.Glide
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.models.SerieElement

class SerieFragment : Fragment() {

    private var _binding: FragmentSerieBinding? = null
    private val binding get() = _binding!!

    private var currentIndex = 0
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var serie: Serie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSerieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serieId = arguments?.getInt("serieId")

        if (serieId == null) {
            Log.e("SerieFragment", "🚨 ERREUR : serieId est NULL !")
            return
        }
        Log.d("SerieFragment", "✅ serieId reçu : $serieId")

        // Récupérer la série en fonction de l'ID
        serie = getSerieById(serieId) ?: return

        if (serie == null) {
            Log.e("SerieFragment", "🚨 ERREUR : Aucune série trouvée pour ID = $serieId")
        } else {
            Log.d("SerieFragment", "✅ Série trouvée : ${serie?.name}")
            loadSerieElement()
        }

        // Charger la première image et le son
        loadSerieElement()

        // Bouton pour rejouer le son
        binding.btnPlaySound.setOnClickListener {
            playAudio(serie.elements[currentIndex].audioResId)
        }

        // Bouton pour aller à l’image suivante
        binding.btnNext.setOnClickListener {
            if (currentIndex < serie.elements.size - 1) {
                currentIndex++
                loadSerieElement()
            }
        }
        binding.btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadSerieElement()
            }
        }
        binding.btnReturnHome.setOnClickListener {
            findNavController().navigate(R.id.action_serieFragment_to_homeFragment)
        }
    }

    private fun loadSerieElement() {
        val currentElement = serie.elements[currentIndex]
        Log.d("SerieFragment", "Serie chargée : ${serie.name}")
        Log.d("SerieFragment", "Nombre d'éléments : ${serie.elements.size}")
        if (currentElement.imageResId != 0) {
            binding.imageSerie.setImageResource(currentElement.imageResId)
        } else {
            binding.imageSerie.setImageResource(R.drawable.placeholder)
        }
        Log.d("SerieFragment", "Chargement de l'image : ${currentElement.imageResId}")
        // Jouer le son
        playAudio(currentElement.audioResId)
    }

    private fun playAudio(audioResId: Int) {
        // Arrêter et libérer l'ancien MediaPlayer
        mediaPlayer?.release()

        // Créer un nouveau MediaPlayer
        mediaPlayer = MediaPlayer.create(requireContext(), audioResId)
        mediaPlayer?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun getSerieById(id: Int): Serie? {
        val seriesList = listOf(
            Serie(1,
                "Animaux",
                R.drawable.serie_animaux,
                elements = listOf(
                    SerieElement(R.drawable.lion, R.raw.lion_sound),
                    SerieElement(R.drawable.elephant, R.raw.elephant_sound),
                    SerieElement(R.drawable.eagle, R.raw.eagle_sound)
                )),
            Serie(2,
                "Météo",
                R.drawable.serie_meteo,
                elements = listOf(
                    SerieElement(R.drawable.lion, R.raw.lion_sound),
                    SerieElement(R.drawable.elephant, R.raw.elephant_sound),
                    SerieElement(R.drawable.eagle, R.raw.eagle_sound)
                )),
            Serie(3,
                "Transport",
                R.drawable.serie_transport,
                elements = listOf(
                    SerieElement(R.drawable.lion, R.raw.lion_sound),
                    SerieElement(R.drawable.elephant, R.raw.elephant_sound),
                    SerieElement(R.drawable.eagle, R.raw.eagle_sound)
                ))
        )

        return seriesList.find { it.id == id }
    }
}
