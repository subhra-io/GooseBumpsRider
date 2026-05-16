package com.goosebumps.rider.data.repository

import com.goosebumps.rider.BuildConfig
import com.goosebumps.rider.data.remote.api.GeoapifyApiService
import com.goosebumps.rider.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

data class RouteResult(
    val points: List<Pair<Double, Double>>,   // lat/lng pairs for the polyline
    val distanceMeters: Double,
    val durationSeconds: Double
)

@Singleton
class RoutingRepository @Inject constructor(
    private val geoapifyApi: GeoapifyApiService
) {
    private val apiKey = BuildConfig.GEOAPIFY_API_KEY

    /**
     * Fetch a driving route between two coordinates.
     * Returns a list of (lat, lng) pairs suitable for GeoapifyMapView routePoints.
     */
    suspend fun getRoute(
        fromLat: Double, fromLng: Double,
        toLat: Double, toLng: Double
    ): Result<RouteResult> {
        return try {
            val waypoints = "$fromLat,$fromLng|$toLat,$toLng"
            val response = geoapifyApi.getRoute(waypoints = waypoints, apiKey = apiKey)
            if (response.isSuccessful) {
                val feature = response.body()?.features?.firstOrNull()
                val coords = feature?.geometry?.coordinates
                    ?.flatten()                          // MultiLineString → flat list
                    ?.map { it[1] to it[0] }             // GeoJSON is [lng, lat] → flip to (lat, lng)
                    ?: emptyList()
                val distance = feature?.properties?.distanceMeters ?: 0.0
                val duration = feature?.properties?.timeSeconds ?: 0.0
                Result.Success(RouteResult(coords, distance, duration))
            } else {
                Result.Error("Route fetch failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    /**
     * Reverse geocode a coordinate to a readable address string.
     */
    suspend fun reverseGeocode(lat: Double, lng: Double): Result<String> {
        return try {
            val response = geoapifyApi.reverseGeocode(lat, lng, apiKey)
            if (response.isSuccessful) {
                val address = response.body()?.features
                    ?.firstOrNull()?.properties?.formatted
                    ?: "$lat, $lng"
                Result.Success(address)
            } else {
                Result.Success("$lat, $lng")
            }
        } catch (e: Exception) {
            Result.Success("$lat, $lng")   // graceful fallback
        }
    }
}
