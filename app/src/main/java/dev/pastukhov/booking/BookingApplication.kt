package dev.pastukhov.booking

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Application class for Booking App.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class BookingApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        initializeLanguage()
    }

    private fun initializeLanguage() {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            LanguageEntryPoint::class.java
        )
        val settingsRepository = entryPoint.settingsRepository()

        applicationScope.launch {
            try {
                val settings = settingsRepository.userSettings.first()
                val locale = when (settings.language.code) {
                    "en" -> "en"
                    "es" -> "es"
                    else -> null
                }
                locale?.let {
                    val appLocale = LocaleListCompat.forLanguageTags(it)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            } catch (e: Exception) {
                // Ignore errors - use system default language
            }
        }
    }
}

@dagger.hilt.EntryPoint
@InstallIn(SingletonComponent::class)
interface LanguageEntryPoint {
    fun settingsRepository(): UserSettingsRepository
}
