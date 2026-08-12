package com.example.authenticatorapp.domain.models

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base32
import java.nio.ByteBuffer

object TotpGenerator {
    private const val TIME_STEP = 30L
    private const val TOTP_LENGTH = 6

    data class TotpResult(
        val code: String,
        val secondsRemaining: Int,
        val progress: Float
    )

    fun generate(secret: String): TotpResult {
        val currentTime = System.currentTimeMillis() / 1000
        val counter = currentTime / TIME_STEP
        val secondsRemaining = (TIME_STEP - (currentTime % TIME_STEP)).toInt()
        val progress = 1f - (secondsRemaining.toFloat() / TIME_STEP.toFloat())

        // Декодируем Base32 секрет (используем Apache Commons Codec)
        val base32 = Base32()
        val key = base32.decode(secret.uppercase())

        // HMAC-SHA1
        val hash = hmacSha1(key, longToBytes(counter))

        // Dynamic truncation (RFC 6238)
        val offset = hash[hash.size - 1].toInt() and 0xF
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        val otp = (binary % 1_000_000).toString().padStart(TOTP_LENGTH, '0')

        return TotpResult(
            code = otp,
            secondsRemaining = secondsRemaining,
            progress = progress
        )
    }

    private fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key, "HmacSHA1"))
        return mac.doFinal(data)
    }

    private fun longToBytes(value: Long): ByteArray {
        val buffer = ByteBuffer.allocate(8)
        buffer.putLong(value)
        return buffer.array()
    }
}