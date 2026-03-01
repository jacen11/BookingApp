package dev.pastukhov.booking.presentation.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.pastukhov.booking.R
import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme

/**
 * Profile screen with user info and settings.
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToBookings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        ProfileHeader(
            user = uiState.user,
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

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Settings Section
        SectionTitle(stringResource(R.string.settings_title))
        
        LanguageSetting(
            currentLanguage = uiState.language,
            onLanguageChange = viewModel::setLanguage
        )
        
        ThemeSetting(
            currentTheme = uiState.theme,
            onThemeChange = viewModel::setTheme
        )
        
        NotificationsSetting(
            enabled = uiState.notificationsEnabled,
            onToggle = viewModel::setNotifications
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.logout))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHeader(
    user: dev.pastukhov.booking.domain.model.User?,
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
                model = user?.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user?.email ?: "",
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
