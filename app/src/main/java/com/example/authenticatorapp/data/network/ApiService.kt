package com.example.authenticatorapp.data.network

import com.example.authenticatorapp.domain.models.Account
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CreateAccountRequest(
    val service: String,
    val username: String,
    val secret: String,
    val icon: String? = null
)

class ApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                //encodeDefault = true
            })
        }
    }

    // Для эмулятора: 10.0.2.2
    // Для реального устройства: IP твоего компьютера
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getAccounts(): List<Account> {
        return client.get("$baseUrl/api/accounts").body()
    }

    suspend fun createAccount(account: Account): Account {
        val request = CreateAccountRequest(
            service = account.service,
            username = account.username,
            secret = account.secret,
            icon = account.icon
        )
        return client.post("$baseUrl/api/accounts") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteAccount(id: Int) {
        client.delete("$baseUrl/api/accounts/$id")
    }
}