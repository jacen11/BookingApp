package dev.pastukhov.booking.presentation.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme
import dev.pastukhov.booking.domain.model.User
import dev.pastukhov.booking.presentation.viewmodel.ProfileViewModel

/**
 * Profile screen with user info and settings.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onNavigateToBookings: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreenContent(
        user = uiState.user,
        language = uiState.language,
        theme = uiState.theme,
        notificationsEnabled = uiState.notificationsEnabled,
        onLanguageChange = viewModel::setLanguage,
        onThemeChange = viewModel::setTheme,
        onNotificationsToggle = viewModel::setNotifications,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        onNavigateToBookings = onNavigateToBookings,
        modifier = modifier
    )
}

@Composable
fun ProfileScreenContent(
    user: User,
    language: AppLanguage,
    theme: AppTheme,
    notificationsEnabled: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (AppTheme) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onNavigateToBookings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        ProfileHeader(
            user = user,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // My Data Section
        SectionTitle(stringResource(R.string.profile_my_data))
        ProfileMenuItem(
            icon = Icons.Default.CalendarMonth,
            title = stringResource(R.string.my_bookings),
            onClick = onNavigateToBookings
        )
        ProfileMenuItem(
            icon = Icons.Default.Favorite,
            title = stringResource(R.string.profile_favorites),
            onClick = {}
        )
        ProfileMenuItem(
            icon = Icons.Default.Payment,
            title = stringResource(R.string.profile_payment),
            onClick = {}
        )
        ProfileMenuItem(
            icon = Icons.Default.LocationOn,
            title = stringResource(R.string.profile_addresses),
            onClick = {}
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Settings Section
        SectionTitle(stringResource(R.string.settings_title))

        LanguageSetting(
            currentLanguage = language,
            onLanguageChange = onLanguageChange
        )

        ThemeSetting(
            currentTheme = theme,
            onThemeChange = onThemeChange
        )

        NotificationsSetting(
            enabled = notificationsEnabled,
            onToggle = onNotificationsToggle
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.logout))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHeader(
    user: User,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(onClick = {}) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.edit_profile))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun LanguageSetting(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(stringResource(R.string.settings_language)) },
        leadingContent = { Icon(Icons.Default.Language, contentDescription = null) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "English"
                        AppLanguage.SPANISH -> "Español"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("English") },
                        onClick = {
                            onLanguageChange(AppLanguage.ENGLISH)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Español") },
                        onClick = {
                            onLanguageChange(AppLanguage.SPANISH)
                            expanded = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun ThemeSetting(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(stringResource(R.string.settings_theme)) },
        leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (currentTheme) {
                        AppTheme.LIGHT -> stringResource(R.string.theme_light)
                        AppTheme.DARK -> stringResource(R.string.theme_dark)
                        AppTheme.SYSTEM -> stringResource(R.string.theme_system)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.theme_light)) },
                        onClick = {
                            onThemeChange(AppTheme.LIGHT)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.theme_dark)) },
                        onClick = {
                            onThemeChange(AppTheme.DARK)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.theme_system)) },
                        onClick = {
                            onThemeChange(AppTheme.SYSTEM)
                            expanded = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun NotificationsSetting(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(stringResource(R.string.settings_notifications)) },
        leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null) },
        trailingContent = {
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenContentPreview() {
    MaterialTheme {
        ProfileScreenContent(
            user = User(
                id = "1",
                email = "demo@booking.app",
                name = "Juan Pérez",
                phone = "+52 123 456 7890",
                avatarUrl = "https://i.pravatar.cc/150?u=user_001"
            ),
            language = AppLanguage.ENGLISH,
            theme = AppTheme.SYSTEM,
            notificationsEnabled = true,
            onLanguageChange = {},
            onThemeChange = {},
            onNotificationsToggle = {},
            onLogout = {},
            onNavigateToBookings = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileHeaderPreview() {
    MaterialTheme {
        ProfileHeader(
            user = User(
                id = "1",
                email = "demo@booking.app",
                name = "Juan Pérez",
                phone = "+52 123 456 7890",
                avatarUrl = "https://i.pravatar.cc/150?u=user_001"
            )
        )
    }
}
