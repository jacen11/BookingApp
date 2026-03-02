package dev.pastukhov.booking.presentation.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pastukhov.booking.R
import dev.pastukhov.booking.presentation.viewmodel.SplashDestination
import dev.pastukhov.booking.presentation.viewmodel.SplashViewModel

/**
 * Splash Screen with animated logo and app branding.
 * Handles authentication check and navigates to appropriate screen.
 *
 * Features:
 * - Fade-in + Scale animation for logo (1.5-2 seconds)
 * - Minimum display duration for smooth UX
 * - Dark/Light theme support via Material 3
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animation values
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }

    // Trigger animations on screen entry
    LaunchedEffect(Unit) {
        // Animate scale from 0 to 1
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        // Animate alpha from 0 to 1
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }

    // Handle navigation based on auth state
    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            SplashDestination.Login -> onNavigateToLogin()
            SplashDestination.Home -> onNavigateToHome()
            null -> { /* Still loading, do nothing */ }
        }
    }

    // Splash UI
    SplashContent(
        logoScale = logoScale.value,
        logoAlpha = logoAlpha.value
    )
}

/**
 * Splash screen content with animated logo and app name.
 */
@Composable
private fun SplashContent(
    logoScale: Float,
    logoAlpha: Float
) {
    // Get brand colors from MaterialTheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.9f),
                        primaryColor.copy(alpha = 0.7f),
                        surfaceColor
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo
            Image(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name with fade animation
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = onPrimaryColor.copy(alpha = logoAlpha),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline with fade animation (slightly delayed)
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = onPrimaryColor.copy(alpha = logoAlpha * 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}
