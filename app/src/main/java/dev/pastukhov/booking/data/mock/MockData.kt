package dev.pastukhov.booking.data.mock

import dev.pastukhov.booking.data.remote.dto.BookingDto
import dev.pastukhov.booking.data.remote.dto.ProviderDto
import dev.pastukhov.booking.data.remote.dto.ServiceDto
import dev.pastukhov.booking.data.remote.dto.UserDto

/**
 * Mock data source for demonstration purposes.
 * Provides sample data without requiring a real backend.
 */
object MockData {

    // Sample user
    val mockUser = UserDto(
        id = "user_001",
        email = "demo@booking.app",
        name = "Juan Pérez",
        phone = "+52 123 456 7890",
        avatarUrl = "https://i.pravatar.cc/150?u=user_001"
    )

    // Sample providers
    val mockProviders = listOf(
        ProviderDto(
            id = "provider_001",
            name = "Studio Belleza María",
            description = "Centro de belleza con más de 15 años de experiencia. Ofrecemos servicios de peluquería, manicure, pedicure y tratamientos faciales.",
            category = "SALON",
            imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400",
            address = "Av. Principal 123",
            city = "Ciudad de México",
            rating = 4.8f,
            reviewCount = 256,
            phone = "+52 55 1234 5678",
            workingHours = "Lun-Sáb: 9:00 - 20:00"
        ),
        ProviderDto(
            id = "provider_002",
            name = "Clínica Dental Sonrisa",
            description = "Clínica dental con tecnología de vanguardia. Especialistas en ortodoncia, implantes y blanqueamiento dental.",
            category = "CLINIC",
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=400",
            address = "Calle Dental 456",
            city = "Ciudad de México",
            rating = 4.9f,
            reviewCount = 189,
            phone = "+52 55 2345 6789",
            workingHours = "Lun-Vie: 8:00 - 19:00"
        ),
        ProviderDto(
            id = "provider_003",
            name = "Barbería Vintage",
            description = "Barbería clásica con ambiente retro. Cortes de pelo, arreglo de barba y tratamientos capilares.",
            category = "BARBER",
            imageUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=400",
            address = "Plaza Central 789",
            city = "Guadalajara",
            rating = 4.7f,
            reviewCount = 142,
            phone = "+52 33 3456 7890",
            workingHours = "Mar-Dom: 10:00 - 21:00"
        ),
        ProviderDto(
            id = "provider_004",
            name = "Spa Relax",
            description = "Tu oasis de tranquilidad. Masajes, tratamientos corporales y aromaterapia.",
            category = "SPA",
            imageUrl = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=400",
            address = "Blvd. Spa 321",
            city = "Monterrey",
            rating = 4.9f,
            reviewCount = 98,
            phone = "+52 81 4567 8901",
            workingHours = "Diario: 9:00 - 22:00"
        ),
        ProviderDto(
            id = "provider_005",
            name = "Gimnasio FitLife",
            description = "Centro de fitness con equipos modernos. Clases grupales, entrenamiento personal y nutrición.",
            category = "FITNESS",
            imageUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400",
            address = "Av. Fitness 654",
            city = "Ciudad de México",
            rating = 4.6f,
            reviewCount = 312,
            phone = "+52 55 5678 9012",
            workingHours = "Lun-Dom: 6:00 - 23:00"
        )
    )

    // Sample services for each provider
    fun getServicesForProvider(providerId: String): List<ServiceDto> {
        return when (providerId) {
            "provider_001" -> listOf(
                ServiceDto(
                    id = "service_001_1",
                    providerId = "provider_001",
                    name = "Corte de cabello",
                    description = "Corte de cabello moderno con lavado",
                    price = 350.0,
                    currency = "MXN",
                    duration = 45,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_001_2",
                    providerId = "provider_001",
                    name = "Manicure",
                    description = "Manicure profesional con esmaltado",
                    price = 250.0,
                    currency = "MXN",
                    duration = 30,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_001_3",
                    providerId = "provider_001",
                    name = "Tratamiento facial",
                    description = "Limpieza facial profunda con mascarilla",
                    price = 500.0,
                    currency = "MXN",
                    duration = 60,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_001_4",
                    providerId = "provider_001",
                    name = "Coloración",
                    description = "Tinte completo con productos profesionales",
                    price = 800.0,
                    currency = "MXN",
                    duration = 90,
                    imageUrl = null
                )
            )
            "provider_002" -> listOf(
                ServiceDto(
                    id = "service_002_1",
                    providerId = "provider_002",
                    name = "Limpieza dental",
                    description = "Limpieza profesional de dientes",
                    price = 600.0,
                    currency = "MXN",
                    duration = 45,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_002_2",
                    providerId = "provider_002",
                    name = "Blanqueamiento",
                    description = "Blanqueamiento dental led",
                    price = 2500.0,
                    currency = "MXN",
                    duration = 60,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_002_3",
                    providerId = "provider_002",
                    name = "Consulta ortodoncia",
                    description = "Evaluación y diagnóstico de ortodoncia",
                    price = 400.0,
                    currency = "MXN",
                    duration = 30,
                    imageUrl = null
                )
            )
            "provider_003" -> listOf(
                ServiceDto(
                    id = "service_003_1",
                    providerId = "provider_003",
                    name = "Corte clásico",
                    description = "Corte de pelo con máquina y tijera",
                    price = 200.0,
                    currency = "MXN",
                    duration = 30,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_003_2",
                    providerId = "provider_003",
                    name = "Arreglo de barba",
                    description = "Arreglo y diseño de barba",
                    price = 150.0,
                    currency = "MXN",
                    duration = 20,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_003_3",
                    providerId = "provider_003",
                    name = "Paquete completo",
                    description = "Corte + barba + lavado",
                    price = 320.0,
                    currency = "MXN",
                    duration = 45,
                    imageUrl = null
                )
            )
            "provider_004" -> listOf(
                ServiceDto(
                    id = "service_004_1",
                    providerId = "provider_004",
                    name = "Masaje relajante",
                    description = "Masaje de 60 minutos",
                    price = 700.0,
                    currency = "MXN",
                    duration = 60,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_004_2",
                    providerId = "provider_004",
                    name = "Masaje reductivo",
                    description = "Masaje modelador con cremas",
                    price = 900.0,
                    currency = "MXN",
                    duration = 45,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_004_3",
                    providerId = "provider_004",
                    name = "Tratamiento spa",
                    description = "Paquete completo: facial + cuerpo",
                    price = 1500.0,
                    currency = "MXN",
                    duration = 120,
                    imageUrl = null
                )
            )
            "provider_005" -> listOf(
                ServiceDto(
                    id = "service_005_1",
                    providerId = "provider_005",
                    name = "Día de spa",
                    description = "Acceso completo por un día",
                    price = 400.0,
                    currency = "MXN",
                    duration = 240,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_005_2",
                    providerId = "provider_005",
                    name = "Entrenamiento personal",
                    description = "Sesión de 1 hora con trainer",
                    price = 500.0,
                    currency = "MXN",
                    duration = 60,
                    imageUrl = null
                ),
                ServiceDto(
                    id = "service_005_3",
                    providerId = "provider_005",
                    name = "Clase grupal",
                    description = "Yoga, spinning, zumba, etc.",
                    price = 150.0,
                    currency = "MXN",
                    duration = 60,
                    imageUrl = null
                )
            )
            else -> emptyList()
        }
    }

    // Sample bookings
    val mockBookings = listOf(
        BookingDto(
            id = "booking_001",
            userId = "user_001",
            providerId = "provider_001",
            providerName = "Studio Belleza María",
            serviceId = "service_001_1",
            serviceName = "Corte de cabello",
            date = "2026-03-01",
            time = "10:00",
            status = "CONFIRMED",
            totalPrice = 350.0,
            notes = null,
            createdAt = "2026-02-25T14:30:00Z"
        ),
        BookingDto(
            id = "booking_002",
            userId = "user_001",
            providerId = "provider_003",
            providerName = "Barbería Vintage",
            serviceId = "service_003_1",
            serviceName = "Corte clásico",
            date = "2026-03-05",
            time = "14:00",
            status = "PENDING",
            totalPrice = 200.0,
            notes = "Primera vez en este lugar",
            createdAt = "2026-02-26T09:15:00Z"
        )
    )

    // Mock login response
    fun login(email: String, password: String): UserDto? {
        return if (email == "demo@booking.app" && password == "demo123") {
            mockUser
        } else null
    }
}
