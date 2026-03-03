package dev.pastukhov.booking.presentation.ui.screens.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pastukhov.booking.domain.model.Provider
import dev.pastukhov.booking.domain.model.ProviderCategory
import dev.pastukhov.booking.presentation.ui.components.ProviderCard

/**
 * List view for displaying search results.
 */
@Composable
fun SearchListView(
    providers: List<Provider>,
    onProviderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(providers, key = { it.id }) { provider ->
            ProviderCard(
                provider = provider,
                onClick = { onProviderClick(provider.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchListViewPreview() {
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
            priceRange = "$$$"
        )
    )

    SearchListView(
        providers = sampleProviders,
        onProviderClick = { }
    )
}
