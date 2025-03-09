package com.example.cartesanimeesapp.models

import com.example.cartesanimeesapp.R

data class Serie(
    val id: Int,
    val name: String,
    //val imageUrl: String,
    val imageResId: Int,
    val elements: List<SerieElement>
)

data class SerieElement(
    //val imageUrl: String,
    //val soundUrl: String
    val imageResId: Int,
    val audioResId: Int
)