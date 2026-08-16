package com.example.authenticatorapp.domain.totp

import org.apache.commons.codec.binary.Base32
import java.security.SecureRandom

object SecretGenerator {

    private val random = SecureRandom()
    private val base32 = Base32()

    /**
     * Генерирует случайный секрет для TOTP
     * @param length Длина секрета в байтах (по умолчанию 10 = 16 символов Base32)
     */
    fun generateSecret(length: Int = 10): String {
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return base32.encodeToString(bytes).replace("=", "")
    }

    /**
     * Проверяет, является ли строка корректным Base32 секретом
     */
    fun isValidSecret(secret: String): Boolean {
        if (secret.isBlank()) return false
        // Base32 содержит только A-Z и 2-7
        val regex = Regex("^[A-Z2-7]+$")
        return regex.matches(secret)
    }
}