package com.example.cartesanimeesapp.models

data class LoginResponse(
    val status: String,
    val patient_id: Int?,
    val nom: String?,
    val prenom: String?,
    val error: String?
)