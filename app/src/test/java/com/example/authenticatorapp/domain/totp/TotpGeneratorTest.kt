package com.example.authenticatorapp.domain.totp

import org.junit.Assert.*
import org.junit.Test

class TotpGeneratorTest {

    private val testSecret = "JBSWY3DPEHPK3PXP"
    private val expectedCode = "492579"

    @Test
    fun `generate TOTP code returns 6 digits`() {
        val result = TotpGenerator.generate(testSecret)
        assertEquals(6, result.code.length)
        assertTrue(result.code.all { it.isDigit() })
    }

    @Test
    fun `generate TOTP code with different secrets returns different codes`() {
        val secret1 = "JBSWY3DPEHPK3PXP"
        val secret2 = "ABCDEFGHIJKLMNOP"

        val result1 = TotpGenerator.generate(secret1)
        val result2 = TotpGenerator.generate(secret2)

        assertNotEquals(result1.code, result2.code)
    }

    @Test
    fun `seconds remaining is between 0 and 30`() {
        val result = TotpGenerator.generate(testSecret)
        assertTrue(result.secondsRemaining in 0..30)
    }

    @Test
    fun `progress is between 0 and 1`() {
        val result = TotpGenerator.generate(testSecret)
        assertTrue(result.progress in 0f..1f)
    }

    @Test
    fun `same secret generates same code at same time`() {
        val result1 = TotpGenerator.generate(testSecret)
        val result2 = TotpGenerator.generate(testSecret)
        assertEquals(result1.code, result2.code)
    }
}