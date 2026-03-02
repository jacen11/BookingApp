package dev.pastukhov.booking.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource

/**
 * Bottom navigation bar component with 4 tabs.
 * Automatically highlights the active tab with animation.
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        BottomNavItem.entries.forEach { item ->
            val isSelected = currentRoute == item.route
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                label = "iconColor"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.titleResId),
                        tint = iconColor
                    )
                },
                label = {
                    Text(text = stringResource(item.titleResId))
                }
            )
        }
    }
}

/**
 * Determines if bottom bar should be visible for the given route.
 * Hidden on detail screens like service_detail, booking_confirmation, etc.
 */
fun shouldShowBottomBar(route: String?): Boolean {
    if (route == null) return false
    
    val hiddenRoutes = listOf(
        "service/",
        "booking/",
        "login",
        "register",
        "splash"
    )
    
    return hiddenRoutes.none { route.contains(it) }
}
