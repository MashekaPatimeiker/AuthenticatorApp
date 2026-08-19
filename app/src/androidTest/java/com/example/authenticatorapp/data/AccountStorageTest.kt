package com.example.authenticatorapp.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.authenticatorapp.data.local.AccountStorage
import com.example.authenticatorapp.domain.models.Account
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountStorageTest {

    private lateinit var accountStorage: AccountStorage

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        accountStorage = AccountStorage(context)
        runBlocking {
            accountStorage.clearAccounts()
        }
    }

    @Test
    fun `save and load accounts`() = runBlocking {
        val accounts = listOf(
            Account(service = "Google", username = "test@gmail.com", secret = "SECRET1"),
            Account(service = "GitHub", username = "testuser", secret = "SECRET2")
        )

        accountStorage.saveAccounts(accounts)
        val loaded = accountStorage.getAccounts().first()

        assertEquals(accounts.size, loaded.size)
        assertEquals(accounts[0].service, loaded[0].service)
    }

    @Test
    fun `clear accounts removes all data`() = runBlocking {
        val accounts = listOf(
            Account(service = "Google", username = "test@gmail.com", secret = "SECRET1")
        )

        accountStorage.saveAccounts(accounts)
        accountStorage.clearAccounts()
        val loaded = accountStorage.getAccounts().first()

        assertTrue(loaded.isEmpty())
    }

    @Test
    fun `load empty accounts returns empty list`() = runBlocking {
        val loaded = accountStorage.getAccounts().first()
        assertTrue(loaded.isEmpty())
    }
}