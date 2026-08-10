package com.example.authenticatorapp.data.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val service: String,
    val username: String,
    val secret: String,
    val icon: String? = null
) {
    @Ignore
    constructor() : this(0, "", "", "", null)
}