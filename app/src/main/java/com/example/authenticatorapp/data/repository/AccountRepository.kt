package com.example.authenticatorapp.data.repository

import com.example.authenticatorapp.data.database.AccountDao
import com.example.authenticatorapp.data.database.AccountEntity
import com.example.authenticatorapp.domain.models.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepository(private val accountDao: AccountDao) {

    fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { entities ->
            entities.map { entity ->
                Account(
                    id = entity.id,
                    service = entity.service,
                    username = entity.username,
                    secret = entity.secret,
                    icon = entity.icon
                )
            }
        }
    }

    suspend fun addAccount(account: Account) {
        val entity = AccountEntity(
            service = account.service,
            username = account.username,
            secret = account.secret,
            icon = account.icon
        )
        accountDao.insertAccount(entity)
    }

    suspend fun deleteAccount(account: Account) {
        val entity = AccountEntity(
            id = account.id,
            service = account.service,
            username = account.username,
            secret = account.secret,
            icon = account.icon
        )
        accountDao.deleteAccount(entity)
    }
}