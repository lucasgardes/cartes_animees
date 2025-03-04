package com.example.cartesanimeesapp.models

data class Serie(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val elements: List<SerieElement>
)

data class SerieElement(
    val imageUrl: String,
    val soundUrl: String
)