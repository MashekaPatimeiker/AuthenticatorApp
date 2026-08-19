package com.example.authenticatorapp.domain.models

import org.junit.Assert.*
import org.junit.Test

class AccountTest {

    @Test
    fun `account can be created with all fields`() {
        val account = Account(
            id = 1,
            service = "Google",
            username = "test@gmail.com",
            secret = "JBSWY3DPEHPK3PXP",
            icon = "google_icon"
        )

        assertEquals(1, account.id)
        assertEquals("Google", account.service)
        assertEquals("test@gmail.com", account.username)
        assertEquals("JBSWY3DPEHPK3PXP", account.secret)
        assertEquals("google_icon", account.icon)
    }

    @Test
    fun `account can be created with default id`() {
        val account = Account(
            service = "GitHub",
            username = "testuser",
            secret = "SECRET"
        )

        assertEquals(0, account.id)
        assertEquals("GitHub", account.service)
    }
}