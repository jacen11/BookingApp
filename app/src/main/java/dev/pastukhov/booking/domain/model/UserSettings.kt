package dev.pastukhov.booking.domain.model

/**
 * User settings model.
 */
data class UserSettings(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true
)

/**
 * App language options.
 */
enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    SPANISH("es");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}

/**
 * App theme options.
 */
enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}
