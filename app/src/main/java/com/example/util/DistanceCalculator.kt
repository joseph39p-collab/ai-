package com.example.util

import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceCalculator {

    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the great-circle distance between two points on the Earth's surface
     * using the Haversine formula.
     *
     * @param lat1 Latitude of point 1 in degrees
     * @param lon1 Longitude of point 1 in degrees
     * @param lat2 Latitude of point 2 in degrees
     * @param lon2 Longitude of point 2 in degrees
     * @return Distance in kilometers
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Formats distance in a human-friendly string (e.g. "650m", "2.3 km").
     */
    fun formatDistance(km: Double): String {
        return when {
            km < 1.0 -> "${(km * 1000).roundToInt()}m"
            km < 10.0 -> String.format(Locale.KOREA, "%.1f km", km)
            km < 100.0 -> String.format(Locale.KOREA, "%.1f km", km)
            else -> "${km.roundToInt()} km"
        }
    }

    /**
     * Estimates travel time based on distance.
     */
    fun estimateTravelTime(km: Double): String {
        return when {
            km <= 1.0 -> "도보 약 ${(km * 15).roundToInt().coerceAtLeast(3)}분"
            km <= 15.0 -> "차량 약 ${(km * 3).roundToInt().coerceAtLeast(8)}분"
            else -> "차량 약 ${(km * 1.5).roundToInt()}분"
        }
    }
}
