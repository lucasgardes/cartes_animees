package com.example.cartesanimeesapp.ui.series

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.databinding.FragmentSerieBinding
import com.example.cartesanimeesapp.models.Serie
import com.example.cartesanimeesapp.models.SerieElement
import com.example.cartesanimeesapp.utils.AudioPlayer
import com.bumptech.glide.Glide
import android.net.Uri

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

        // 🔹 Récupération de la série sélectionnée (exemple avec un mock)
        serie = Serie(
            id = 1,
            name = "Animaux",
            imageUrl = "file:///android_asset/images/lion.jpg",
            elements = listOf(
                SerieElement("file:///android_asset/images/lion.jpg", "file:///android_asset/sounds/lion.mp3"),
                SerieElement("file:///android_asset/images/elephant.jpg", "file:///android_asset/sounds/elephant.mp3"),
                SerieElement("file:///android_asset/images/tiger.jpg", "file:///android_asset/sounds/tiger.mp3")
            )
        )

        updateUI()

        // 🔹 Gérer le bouton "Suivant"
        binding.btnNext.setOnClickListener {
            if (currentIndex < serie.elements.size - 1) {
                currentIndex++
                updateUI()
            }
        }

        // 🔹 Gérer le bouton "Précédent"
        binding.btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateUI()
            }
        }

        // 🔹 Gérer le bouton "Rejouer son"
        binding.btnPlaySound.setOnClickListener {
            playSound()
        }
    }

    private fun updateUI() {
        val currentElement = serie.elements[currentIndex]

        // Charger l'image avec Glide depuis un fichier local ou une URL
        Glide.with(requireContext())
            .load(currentElement.imageUrl)
            .into(binding.imageSerie)

        // Lire le son
        playSound()
    }

    private fun playSound() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(requireContext(), Uri.parse(serie.elements[currentIndex].soundUrl))
        mediaPlayer?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}

