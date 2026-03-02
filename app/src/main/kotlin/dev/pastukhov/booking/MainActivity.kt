package dev.pastukhov.booking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pastukhov.booking.data.repository.UserSettingsRepository
import dev.pastukhov.booking.domain.model.AppTheme
import dev.pastukhov.booking.domain.model.UserSettings
import dev.pastukhov.booking.presentation.navigation.BookingNavHost
import dev.pastukhov.booking.ui.theme.BookingAppTheme
import javax.inject.Inject

/**
 * Main activity for the Booking App.
 * Entry point of the application with Hilt for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsRepository: UserSettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsRepository.userSettings.collectAsState(
                initial = UserSettings()
            )
            
            // Apply language
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(settings.language.code)
            )
            
            // Apply theme
            val darkTheme = when (settings.theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }
            
            BookingAppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    BookingNavHost(
                        navController = navController,
                        userSettingsRepository = settingsRepository
                    )
                }
            }
        }
    }
}
