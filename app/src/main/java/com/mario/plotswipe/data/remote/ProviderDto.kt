package com.mario.plotswipe.data.remote

import com.google.gson.annotations.SerializedName

// 1. La respuesta general
data class ProviderResponse(
    val results: Map<String, CountryProviders>
)

// 2. Lo que hay dentro de cada país (España, EEUU...)
data class CountryProviders(
    val flatrate: List<ProviderInfo>? = null, // flatrate = Plataformas de suscripción (Netflix, Disney+)
    val rent: List<ProviderInfo>? = null,     // Alquiler
    val buy: List<ProviderInfo>? = null       // Compra
)

// 3. Los datos de la plataforma en sí
data class ProviderInfo(
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("logo_path") val logoPath: String?
)