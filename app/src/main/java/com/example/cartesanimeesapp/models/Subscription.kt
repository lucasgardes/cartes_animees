package com.example.cartesanimeesapp.models

data class Subscription(
    val id: Int,
    val stripe_subscription_id: String?,
    val statut: String
)
