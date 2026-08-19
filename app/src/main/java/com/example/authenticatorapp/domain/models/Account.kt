package com.example.authenticatorapp.domain.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
@OptIn(InternalSerializationApi::class)
@Serializable
data class Account(
    val id: Int = 0,
    val service: String,
    val username: String,
    val secret: String,
    val icon: String? = null
)