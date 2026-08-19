package com.example.authenticatorapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.authenticatorapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `main activity shows accounts screen initially`() {
        composeTestRule.setContent {
            MainActivity()
        }

        composeTestRule.onNodeWithText("Аккаунты").assertIsDisplayed()
    }

    @Test
    fun `click on add button navigates to add screen`() {
        composeTestRule.setContent {
            MainActivity()
        }

        composeTestRule.onNodeWithContentDescription("Добавить аккаунт").performClick()
        composeTestRule.onNodeWithText("Добавить аккаунт").assertIsDisplayed()
    }

    @Test
    fun `click on settings button navigates to settings screen`() {
        composeTestRule.setContent {
            MainActivity()
        }

        composeTestRule.onNodeWithContentDescription("Настройки").performClick()
        composeTestRule.onNodeWithText("Настройки").assertIsDisplayed()
    }
}