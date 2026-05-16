package com.goosebumps.rider.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Geoapify Routing API
 * Docs: https://apidocs.geoapify.com/docs/routing/
 */
interface GeoapifyApiService {

    /**
     * Get a driving route between two points.
     * Returns GeoJSON FeatureCollection with the route geometry.
     */
    @GET("v1/routing")
    suspend fun getRoute(
        @Query("waypoints") waypoints: String,   // "lat,lng|lat,lng"
        @Query("mode") mode: String = "drive",
        @Query("apiKey") apiKey: String
    ): Response<GeoapifyRouteResponse>

    /**
     * Reverse geocode a lat/lng to a human-readable address.
     */
    @GET("v1/geocode/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("apiKey") apiKey: String
    ): Response<GeoapifyGeocodeResponse>
}

// ── Response models ──────────────────────────────────────────────────────────

data class GeoapifyRouteResponse(
    @SerializedName("features") val features: List<RouteFeature>?
)

data class RouteFeature(
    @SerializedName("geometry") val geometry: RouteGeometry?,
    @SerializedName("properties") val properties: RouteProperties?
)

data class RouteGeometry(
    @SerializedName("type") val type: String,           // "MultiLineString"
    @SerializedName("coordinates") val coordinates: List<List<List<Double>>>?
)

data class RouteProperties(
    @SerializedName("distance") val distanceMeters: Double?,   // metres
    @SerializedName("time") val timeSeconds: Double?,          // seconds
    @SerializedName("legs") val legs: List<RouteLeg>?
)

data class RouteLeg(
    @SerializedName("distance") val distance: Double?,
    @SerializedName("time") val time: Double?
)

data class GeoapifyGeocodeResponse(
    @SerializedName("features") val features: List<GeocodeFeature>?
)

data class GeocodeFeature(
    @SerializedName("properties") val properties: GeocodeProperties?
)

data class GeocodeProperties(
    @SerializedName("formatted") val formatted: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("street") val street: String?,
    @SerializedName("housenumber") val houseNumber: String?
)
