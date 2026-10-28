package com.example.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CinemaDatabase
import com.example.model.CinemaBrand
import com.example.model.CinemaWithDistance
import com.example.model.FilterState
import com.example.model.SortMode
import com.example.model.SpecialScreen
import com.example.model.UserLocation
import com.example.util.CinemaFilterEngine
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CinemaViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedBrands = MutableStateFlow<Set<CinemaBrand>>(emptySet())
    val selectedBrands = _selectedBrands.asStateFlow()

    private val _selectedSpecialScreens = MutableStateFlow<Set<SpecialScreen>>(emptySet())
    val selectedSpecialScreens = _selectedSpecialScreens.asStateFlow()

    private val _onlySmallOrStandard = MutableStateFlow(false)
    val onlySmallOrStandard = _onlySmallOrStandard.asStateFlow()

    private val _sortMode = MutableStateFlow(SortMode.DISTANCE_ASC)
    val sortMode = _sortMode.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly = _favoritesOnly.asStateFlow()

    private val _userLocation = MutableStateFlow(UserLocation.SEOUL_GANGNAM)
    val userLocation = _userLocation.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds = _favoriteIds.asStateFlow()

    private val _selectedCinemaDetail = MutableStateFlow<CinemaWithDistance?>(null)
    val selectedCinemaDetail = _selectedCinemaDetail.asStateFlow()

    private val _isLocationPickerOpen = MutableStateFlow(false)
    val isLocationPickerOpen = _isLocationPickerOpen.asStateFlow()

    private val _isGpsLoading = MutableStateFlow(false)
    val isGpsLoading = _isGpsLoading.asStateFlow()

    // Combined filter state
    val filterState: StateFlow<FilterState> = combine(
        combine(_searchQuery, _selectedBrands, _selectedSpecialScreens) { q, b, s ->
            Triple(q, b, s)
        },
        _onlySmallOrStandard,
        _sortMode,
        _favoritesOnly
    ) { (query, brands, screens), smallOnly, sort, favs ->
        FilterState(
            searchQuery = query,
            selectedBrands = brands,
            selectedSpecialScreens = screens,
            onlySmallOrStandard = smallOnly,
            sortMode = sort,
            favoritesOnly = favs
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FilterState()
    )

    // Filtered and sorted cinemas
    val filteredCinemas: StateFlow<List<CinemaWithDistance>> = combine(
        filterState,
        _userLocation,
        _favoriteIds
    ) { filter, location, favs ->
        CinemaFilterEngine.filterAndSort(
            cinemas = CinemaDatabase.cinemas,
            userLat = location.latitude,
            userLon = location.longitude,
            filterState = filter,
            favoriteIds = favs
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleBrand(brand: CinemaBrand) {
        val current = _selectedBrands.value
        _selectedBrands.value = if (current.contains(brand)) {
            current - brand
        } else {
            current + brand
        }
    }

    fun toggleSpecialScreen(screen: SpecialScreen) {
        val current = _selectedSpecialScreens.value
        _selectedSpecialScreens.value = if (current.contains(screen)) {
            current - screen
        } else {
            current + screen
        }
    }

    fun toggleOnlySmallOrStandard() {
        _onlySmallOrStandard.value = !_onlySmallOrStandard.value
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
    }

    fun toggleFavoritesOnly() {
        _favoritesOnly.value = !_favoritesOnly.value
    }

    fun toggleFavorite(cinemaId: String) {
        val current = _favoriteIds.value
        _favoriteIds.value = if (current.contains(cinemaId)) {
            current - cinemaId
        } else {
            current + cinemaId
        }
    }

    fun setUserLocation(location: UserLocation) {
        _userLocation.value = location
    }

    fun setLocationPickerOpen(isOpen: Boolean) {
        _isLocationPickerOpen.value = isOpen
    }

    fun openDetail(cinemaWithDistance: CinemaWithDistance) {
        _selectedCinemaDetail.value = cinemaWithDistance
    }

    fun closeDetail() {
        _selectedCinemaDetail.value = null
    }

    fun resetAllFilters() {
        _searchQuery.value = ""
        _selectedBrands.value = emptySet()
        _selectedSpecialScreens.value = emptySet()
        _onlySmallOrStandard.value = false
        _sortMode.value = SortMode.DISTANCE_ASC
        _favoritesOnly.value = false
    }

    @SuppressLint("MissingPermission")
    fun requestCurrentGpsLocation(context: Context) {
        _isGpsLoading.value = true
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    _isGpsLoading.value = false
                    if (location != null) {
                        _userLocation.value = UserLocation(
                            label = "내 현재 위치 (GPS)",
                            latitude = location.latitude,
                            longitude = location.longitude,
                            isGps = true
                        )
                    }
                }
                .addOnFailureListener {
                    _isGpsLoading.value = false
                }
        } catch (e: Exception) {
            _isGpsLoading.value = false
        }
    }
}
