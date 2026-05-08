package dev.pastukhov.booking.presentation.viewmodel

import app.cash.turbine.test
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme
import dev.pastukhov.booking.domain.model.User
import dev.pastukhov.booking.domain.model.UserSettings
import dev.pastukhov.booking.domain.repository.UserRepository
import dev.pastukhov.booking.presentation.model.ProfileEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testSettings = UserSettings(
        language = AppLanguage.ENGLISH,
        theme = AppTheme.SYSTEM,
        notificationsEnabled = true
    )

    @Test
    fun `initial state is correct`() = runTest {
        val userRepository = mock<UserRepository>()
        val settingsRepository = mock<UserSettingsRepository>()
        
        val userFlow = MutableStateFlow<User?>(null)
        val settingsFlow = MutableStateFlow(testSettings)
        whenever(userRepository.getCurrentUser()).thenReturn(userFlow)
        whenever(settingsRepository.userSettings).thenReturn(settingsFlow)

        val viewModel = ProfileViewModel(userRepository, settingsRepository)

        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)
            assertEquals("", initialState.user.id)
            assertEquals(AppLanguage.ENGLISH, initialState.language)
            assertEquals(AppTheme.SYSTEM, initialState.theme)
            assertEquals(true, initialState.notificationsEnabled)
            assertNull(initialState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setLanguage calls repository`() = runTest {
        val userRepository = mock<UserRepository>()
        val settingsRepository = mock<UserSettingsRepository>()
        
        val userFlow = MutableStateFlow<User?>(null)
        val settingsFlow = MutableStateFlow(testSettings)
        whenever(userRepository.getCurrentUser()).thenReturn(userFlow)
        whenever(settingsRepository.userSettings).thenReturn(settingsFlow)

        val viewModel = ProfileViewModel(userRepository, settingsRepository)
        
        viewModel.handleEvent(ProfileEvent.SetLanguage(AppLanguage.SPANISH))
        
        advanceUntilIdle()
        
        verify(settingsRepository).setLanguage(AppLanguage.SPANISH)
    }

    @Test
    fun `setTheme calls repository`() = runTest {
        val userRepository = mock<UserRepository>()
        val settingsRepository = mock<UserSettingsRepository>()
        
        val userFlow = MutableStateFlow<User?>(null)
        val settingsFlow = MutableStateFlow(testSettings)
        whenever(userRepository.getCurrentUser()).thenReturn(userFlow)
        whenever(settingsRepository.userSettings).thenReturn(settingsFlow)

        val viewModel = ProfileViewModel(userRepository, settingsRepository)
        
        viewModel.handleEvent(ProfileEvent.SetTheme(AppTheme.DARK))
        
        advanceUntilIdle()
        
        verify(settingsRepository).setTheme(AppTheme.DARK)
    }

    @Test
    fun `setNotifications calls repository with true`() = runTest {
        val userRepository = mock<UserRepository>()
        val settingsRepository = mock<UserSettingsRepository>()
        
        val userFlow = MutableStateFlow<User?>(null)
        val settingsFlow = MutableStateFlow(testSettings)
        whenever(userRepository.getCurrentUser()).thenReturn(userFlow)
        whenever(settingsRepository.userSettings).thenReturn(settingsFlow)

        val viewModel = ProfileViewModel(userRepository, settingsRepository)
        
        viewModel.handleEvent(ProfileEvent.SetNotifications(true))
        
        advanceUntilIdle()
        
        verify(settingsRepository).setNotifications(true)
    }

    @Test
    fun `setNotifications calls repository with false`() = runTest {
        val userRepository = mock<UserRepository>()
        val settingsRepository = mock<UserSettingsRepository>()
        
        val userFlow = MutableStateFlow<User?>(null)
        val settingsFlow = MutableStateFlow(testSettings)
        whenever(userRepository.getCurrentUser()).thenReturn(userFlow)
        whenever(settingsRepository.userSettings).thenReturn(settingsFlow)

        val viewModel = ProfileViewModel(userRepository, settingsRepository)
        
        viewModel.handleEvent(ProfileEvent.SetNotifications(false))
        
        advanceUntilIdle()
        
        verify(settingsRepository).setNotifications(false)
    }

    @Test
    fun `logout calls clearSettings`() = runTest {
        val userRepository = mock<UserRepository>()
        val settingsRepository = mock<UserSettingsRepository>()
        
        val userFlow = MutableStateFlow<User?>(null)
        val settingsFlow = MutableStateFlow(testSettings)
        whenever(userRepository.getCurrentUser()).thenReturn(userFlow)
        whenever(settingsRepository.userSettings).thenReturn(settingsFlow)

        val viewModel = ProfileViewModel(userRepository, settingsRepository)
        
        viewModel.handleEvent(ProfileEvent.Logout)
        
        advanceUntilIdle()
        
        verify(settingsRepository).clearSettings()
    }
}