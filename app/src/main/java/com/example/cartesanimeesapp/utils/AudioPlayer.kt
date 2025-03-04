package com.example.cartesanimeesapp.utils

import android.content.Context
import android.media.MediaPlayer

object AudioPlayer {
    private var player: MediaPlayer? = null

    fun playSound(context: Context, soundResId: Int) {
        stopSound() // Arrêter le son en cours
        player = MediaPlayer.create(context, soundResId)
        player?.start()
    }

    fun stopSound() {
        player?.stop()
        player?.release()
        player = null
    }
}
