package com.example.authenticatorapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: Int = 0,
    val service: String,
    val username: String,
    val secret: String,
    val icon: String? = null
)