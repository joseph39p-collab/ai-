package com.example.util

import com.example.model.Cinema
import com.example.model.CinemaWithDistance
import com.example.model.FilterState
import com.example.model.SortMode
import com.example.model.SpecialScreen

object CinemaFilterEngine {

    /**
     * Filters and sorts the list of cinemas based on the user's current location,
     * search query, brand selection, and special screen requirements (AND condition).
     */
    fun filterAndSort(
        cinemas: List<Cinema>,
        userLat: Double,
        userLon: Double,
        filterState: FilterState,
        favoriteIds: Set<String> = emptySet()
    ): List<CinemaWithDistance> {
        val query = filterState.searchQuery.trim().lowercase()

        val filtered = cinemas.filter { cinema ->
            // 1. Search Query filter (matches name, branch, address, region, or special screen name)
            if (query.isNotEmpty()) {
                val matchesQuery = cinema.name.lowercase().contains(query) ||
                        cinema.branchName.lowercase().contains(query) ||
                        cinema.address.lowercase().contains(query) ||
                        cinema.region.lowercase().contains(query) ||
                        cinema.brand.displayName.lowercase().contains(query) ||
                        cinema.specialScreens.any { it.displayName.lowercase().contains(query) || it.shortName.lowercase().contains(query) }

                if (!matchesQuery) return@filter false
            }

            // 2. Favorites only toggle
            if (filterState.favoritesOnly && !favoriteIds.contains(cinema.id)) {
                return@filter false
            }

            // 3. Brand Filter (OR across selected brands; if empty, all brands match)
            if (filterState.selectedBrands.isNotEmpty()) {
                if (cinema.brand !in filterState.selectedBrands) {
                    return@filter false
                }
            }

            // 4. Small / Standard Screen Only filter
            if (filterState.onlySmallOrStandard) {
                val isStandardOrSmall = cinema.isSmallOrStandardOnly ||
                        (!cinema.hasImax && !cinema.has4d && !cinema.hasDolby)
                if (!isStandardOrSmall) {
                    return@filter false
                }
            }

            // 5. Special Screen Filter (Strict AND condition: ALL selected special screens must be present)
            if (filterState.selectedSpecialScreens.isNotEmpty()) {
                for (screen in filterState.selectedSpecialScreens) {
                    val hasScreen = when (screen) {
                        SpecialScreen.IMAX -> cinema.hasImax
                        SpecialScreen.FOUR_D -> cinema.has4d
                        SpecialScreen.DOLBY -> cinema.hasDolby
                        SpecialScreen.SCREEN_X -> cinema.hasScreenX
                        SpecialScreen.RECLINER -> cinema.hasRecliner
                    }
                    if (!hasScreen) {
                        return@filter false
                    }
                }
            }

            true
        }

        // Attach computed distances
        val withDistance = filtered.map { cinema ->
            val distKm = DistanceCalculator.calculateDistanceKm(
                userLat,
                userLon,
                cinema.latitude,
                cinema.longitude
            )
            CinemaWithDistance(
                cinema = cinema,
                distanceKm = distKm,
                isFavorite = favoriteIds.contains(cinema.id)
            )
        }

        // Sort results
        return when (filterState.sortMode) {
            SortMode.DISTANCE_ASC -> withDistance.sortedBy { it.distanceKm }
            SortMode.NAME_ASC -> withDistance.sortedBy { it.cinema.name }
            SortMode.SPECIAL_COUNT_DESC -> withDistance.sortedWith(
                compareByDescending<CinemaWithDistance> { it.cinema.specialScreens.size }
                    .thenBy { it.distanceKm }
            )
        }
    }
}
