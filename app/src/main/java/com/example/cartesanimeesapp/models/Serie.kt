package com.example.cartesanimeesapp.models

import com.example.cartesanimeesapp.R
import com.google.gson.annotations.SerializedName

data class Serie(
    val id: Int,
    val nom: String,
    val image_path: String,
    val elements: List<SerieElement>
)

data class SerieElement(
    val id: Int,
    @SerializedName("image_cartoon") val image_cartoon: String,
    @SerializedName("image_real") val image_real: String,
    @SerializedName("son_path") val son_path: String
)