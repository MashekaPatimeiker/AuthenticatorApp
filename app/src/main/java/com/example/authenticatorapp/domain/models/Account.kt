package com.example.authenticatorapp.domain.models

data class Account(
    val id: Int = 0,
    val service: String,
    val username: String,
    val secret: String,
    val icon: String? = null
)