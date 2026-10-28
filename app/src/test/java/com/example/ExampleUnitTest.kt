package com.example

import com.example.data.CinemaDatabase
import com.example.model.CinemaBrand
import com.example.model.FilterState
import com.example.model.SortMode
import com.example.model.SpecialScreen
import com.example.model.UserLocation
import com.example.util.CinemaFilterEngine
import com.example.util.DistanceCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun distanceCalculator_calculatesCorrectDistance() {
        // Gangnam Station (37.4979, 127.0276) to Megabox COEX (37.5119, 127.0592)
        val dist = DistanceCalculator.calculateDistanceKm(
            37.4979, 127.0276,
            37.5119, 127.0592
        )
        // Expected ~3.2km
        assertTrue("Distance should be between 2.5 and 4.0 km, got: $dist", dist in 2.5..4.0)

        // Test formatting
        val formatted = DistanceCalculator.formatDistance(dist)
        assertTrue(formatted.contains("km"))
    }

    @Test
    fun filterEngine_brandAndSpecialScreenAndNearest_filtersCorrectly() {
        // Select CGV + IMAX + Nearest from Gangnam
        val filter = FilterState(
            selectedBrands = setOf(CinemaBrand.CGV),
            selectedSpecialScreens = setOf(SpecialScreen.IMAX),
            sortMode = SortMode.DISTANCE_ASC
        )

        val results = CinemaFilterEngine.filterAndSort(
            cinemas = CinemaDatabase.cinemas,
            userLat = UserLocation.SEOUL_GANGNAM.latitude,
            userLon = UserLocation.SEOUL_GANGNAM.longitude,
            filterState = filter
        )

        assertFalse("Results should not be empty", results.isEmpty())

        // Verify every returned item is CGV AND has IMAX
        results.forEach { item ->
            assertEquals(CinemaBrand.CGV, item.cinema.brand)
            assertTrue("Cinema must have IMAX", item.cinema.hasImax)
        }

        // Verify sorted by distance ascending
        for (i in 0 until results.size - 1) {
            assertTrue(
                "Must be sorted by distance asc",
                results[i].distanceKm <= results[i + 1].distanceKm
            )
        }
    }

    @Test
    fun filterEngine_dolbyCinemaFilter_returnsOnlyDolbyTheaters() {
        val filter = FilterState(
            selectedSpecialScreens = setOf(SpecialScreen.DOLBY)
        )

        val results = CinemaFilterEngine.filterAndSort(
            cinemas = CinemaDatabase.cinemas,
            userLat = UserLocation.SEOUL_GANGNAM.latitude,
            userLon = UserLocation.SEOUL_GANGNAM.longitude,
            filterState = filter
        )

        assertFalse("Results should not be empty", results.isEmpty())
        results.forEach { item ->
            assertTrue(item.cinema.hasDolby)
        }
    }

    @Test
    fun filterEngine_searchQuery_matchesTheaters() {
        val filter = FilterState(
            searchQuery = "용산"
        )

        val results = CinemaFilterEngine.filterAndSort(
            cinemas = CinemaDatabase.cinemas,
            userLat = UserLocation.SEOUL_GANGNAM.latitude,
            userLon = UserLocation.SEOUL_GANGNAM.longitude,
            filterState = filter
        )

        assertTrue(results.any { it.cinema.name.contains("용산") })
    }

    @Test
    fun cinemaDatabase_flagshipCinemasArePresent() {
        val yongsan = CinemaDatabase.cinemas.find { it.id == "cgv_yongsan" }
        val coex = CinemaDatabase.cinemas.find { it.id == "megabox_coex" }
        val worldtower = CinemaDatabase.cinemas.find { it.id == "lotte_worldtower" }

        assertTrue("CGV Yongsan must exist", yongsan != null && yongsan.hasImax && yongsan.has4d)
        assertTrue("Megabox Coex must exist", coex != null && coex.hasDolby)
        assertTrue("Lotte World Tower must exist", worldtower != null && worldtower.has4d && worldtower.hasDolby)
    }

    @Test
    fun filterEngine_reclinerFilter_returnsOnlyReclinerTheaters() {
        val filter = FilterState(
            selectedSpecialScreens = setOf(SpecialScreen.RECLINER)
        )

        val results = CinemaFilterEngine.filterAndSort(
            cinemas = CinemaDatabase.cinemas,
            userLat = UserLocation.SEOUL_GANGNAM.latitude,
            userLon = UserLocation.SEOUL_GANGNAM.longitude,
            filterState = filter
        )

        assertFalse("Recliner results should not be empty", results.isEmpty())
        results.forEach { item ->
            assertTrue("Cinema must have recliner", item.cinema.hasRecliner)
        }
        assertTrue("Should contain CineQ Sindorim", results.any { it.cinema.id == "cineq_sindorim" })
        assertTrue("Should contain Megabox Central", results.any { it.cinema.id == "megabox_central" })
    }

    @Test
    fun filterEngine_smallOrStandardFilter_returnsSmallAndStandardOnlyTheaters() {
        val filter = FilterState(
            onlySmallOrStandard = true
        )

        val results = CinemaFilterEngine.filterAndSort(
            cinemas = CinemaDatabase.cinemas,
            userLat = UserLocation.SEOUL_GANGNAM.latitude,
            userLon = UserLocation.SEOUL_GANGNAM.longitude,
            filterState = filter
        )

        assertFalse("Small/standard results should not be empty", results.isEmpty())
        results.forEach { item ->
            val isStandardOrSmall = item.cinema.isSmallOrStandardOnly ||
                    (!item.cinema.hasImax && !item.cinema.has4d && !item.cinema.hasDolby)
            assertTrue("Must be standard or small cinema", isStandardOrSmall)
        }
        assertTrue("Should contain The Forest Cinema", results.any { it.cinema.id == "the_forest_cinema" })
        assertTrue("Should contain Laika Cinema", results.any { it.cinema.id == "laika_cinema" })
        assertTrue("Should contain CGV Hagye", results.any { it.cinema.id == "cgv_hagye" })
    }
}
