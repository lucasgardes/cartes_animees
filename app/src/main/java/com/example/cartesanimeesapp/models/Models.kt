package com.example.cartesanimeesapp.models

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val status: String, val message: String)

data class Series(val id: Int, val name: String, val description: String)
