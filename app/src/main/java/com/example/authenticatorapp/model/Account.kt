package com.example.authenticatorapp.model

data class Account(
    val id: Long = 0,
    val service: String,
    val username: String,
    val secret: String,
    val icon: String? = null
)