package dev.pastukhov.booking

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Booking App.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class BookingApplication : Application()
