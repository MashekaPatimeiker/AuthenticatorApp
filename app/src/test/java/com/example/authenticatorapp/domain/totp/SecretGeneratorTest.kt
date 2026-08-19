package com.example.authenticatorapp.domain.totp

import org.junit.Assert.*
import org.junit.Test

class SecretGeneratorTest {

    @Test
    fun `generateSecret returns base32 string`() {
        val secret = SecretGenerator.generateSecret()
        assertTrue(secret.matches(Regex("^[A-Z2-7]+$")))
    }

    @Test
    fun `generateSecret returns correct length`() {
        val length = 10
        val secret = SecretGenerator.generateSecret(length)
        assertEquals(16, secret.length)
    }

    @Test
    fun `isValidSecret validates correct secret`() {
        val validSecret = "JBSWY3DPEHPK3PXP"
        assertTrue(SecretGenerator.isValidSecret(validSecret))
    }

    @Test
    fun `isValidSecret rejects invalid secret`() {
        val invalidSecret = "12345!@#"
        assertFalse(SecretGenerator.isValidSecret(invalidSecret))
    }

    @Test
    fun `isValidSecret rejects empty string`() {
        assertFalse(SecretGenerator.isValidSecret(""))
    }

    @Test
    fun `generateSecret produces different values each time`() {
        val secret1 = SecretGenerator.generateSecret()
        val secret2 = SecretGenerator.generateSecret()
        assertNotEquals(secret1, secret2)
    }
}