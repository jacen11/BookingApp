package dev.pastukhov.booking.presentation.ui.screens.search.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory

/**
 * Map view for displaying search results with markers.
 */
@Composable
fun SearchMapView(
    providers: List<Provider>,
    onProviderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mexicoCity = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mexicoCity, 12f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        providers.forEach { provider ->
            Marker(
                state = MarkerState(
                    position = LatLng(provider.latitude, provider.longitude)
                ),
                title = provider.name,
                snippet = "${provider.rating} ⭐ • ${provider.priceRange}",
                onInfoWindowClick = {
                    onProviderClick(provider.id)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchMapViewPreview() {
    val sampleProviders = listOf(
        Provider(
            id = "1",
            name = "Salón de Belleza Luxe",
            description = "Centro de belleza y spa",
            category = ProviderCategory.SALON,
            address = "Av. Principal 123",
            city = "Ciudad de México",
            rating = 4.5f,
            reviewCount = 128,
            phone = "+52 55 1234 5678",
            workingHours = "9:00 - 20:00",
            latitude = 19.4326,
            longitude = -99.1332,
            priceRange = "$$"
        ),
        Provider(
            id = "2",
            name = "Barbería Clásica",
            description = "Corte y afeitado tradicional",
            category = ProviderCategory.BARBER,
            address = "Calle Secundaria 45",
            city = "Ciudad de México",
            rating = 4.8f,
            reviewCount = 256,
            phone = "+52 55 8765 4321",
            workingHours = "10:00 - 21:00",
            latitude = 19.4450,
            longitude = -99.1400,
            priceRange = "$"
        ),
        Provider(
            id = "3",
            name = "Spa Wellness",
            description = "Tratamientos de relajación",
            category = ProviderCategory.SPA,
            address = "Blvd. Salud 78",
            city = "Ciudad de México",
            rating = 4.9f,
            reviewCount = 89,
            phone = "+52 55 5555 5555",
            workingHours = "8:00 - 22:00",
            latitude = 19.4200,
            longitude = -99.1500,
            priceRange = "$$$"
        )
    )

    SearchMapView(
        providers = sampleProviders,
        onProviderClick = { }
    )
}
