package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.SortMode
import com.example.ui.components.CinemaCard
import com.example.ui.components.CinemaDetailSheet
import com.example.ui.components.FilterChipsBar
import com.example.ui.components.LocationPickerSheet
import com.example.ui.theme.CineRed
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.util.NaverLinkHandler
import com.example.viewmodel.CinemaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaMainScreen(
    viewModel: CinemaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isGpsLoading by viewModel.isGpsLoading.collectAsState()
    val filteredCinemas by viewModel.filteredCinemas.collectAsState()
    val selectedCinemaDetail by viewModel.selectedCinemaDetail.collectAsState()
    val isLocationPickerOpen by viewModel.isLocationPickerOpen.collectAsState()

    var isSortDropdownOpen by remember { mutableStateOf(false) }

    // Location Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.requestCurrentGpsLocation(context)
        }
    }

    val handleGpsRequest = {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            viewModel.requestCurrentGpsLocation(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                // Top Brand Row + Location Picker trigger
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Logo & Title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CineRed,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "씨네파인더",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "CineFinder",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "위치 기반 특수관 & 영화관 필터",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Current Location Chip / Picker
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .clickable { viewModel.setLocationPickerOpen(true) }
                            .testTag("location_picker_trigger")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (userLocation.isGps) Color(0xFF10B981) else Color(0xFF60A5FA),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = userLocation.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("search_text_input"),
                    placeholder = {
                        Text(
                            text = "지역(강남, 부산) 또는 극장(용산, 코엑스) 검색",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "검색어 지우기",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Filter Chips Bar
                FilterChipsBar(
                    filterState = filterState,
                    onToggleBrand = { viewModel.toggleBrand(it) },
                    onToggleSpecialScreen = { viewModel.toggleSpecialScreen(it) },
                    onToggleOnlySmallOrStandard = { viewModel.toggleOnlySmallOrStandard() },
                    onToggleSortNearest = {
                        val currentMode = filterState.sortMode
                        if (currentMode == SortMode.DISTANCE_ASC) {
                            viewModel.setSortMode(SortMode.NAME_ASC)
                        } else {
                            viewModel.setSortMode(SortMode.DISTANCE_ASC)
                        }
                    },
                    onToggleFavorites = { viewModel.toggleFavoritesOnly() },
                    onResetFilters = { viewModel.resetAllFilters() }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Results Bar: Count + Sort mode dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "총 ${filteredCinemas.size}개의 영화관",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Sort Dropdown Trigger
                Box {
                    Surface(
                        color = DarkSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .clickable { isSortDropdownOpen = true }
                            .testTag("sort_mode_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = filterState.sortMode.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isSortDropdownOpen,
                        onDismissRequest = { isSortDropdownOpen = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        SortMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = mode.displayName,
                                        fontWeight = if (mode == filterState.sortMode) FontWeight.Bold else FontWeight.Normal,
                                        color = if (mode == filterState.sortMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.setSortMode(mode)
                                    isSortDropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }

            // Cinema List or Empty State
            if (filteredCinemas.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = DarkSurfaceVariant,
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "조건에 일치하는 영화관이 없습니다",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "선택하신 브랜드나 특수관(IMAX, 4D, 돌비) 조건을 조정해보세요.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.resetAllFilters() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CineRed
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("empty_state_reset_button")
                        ) {
                            Text("필터 전체 초기화")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("cinema_list_view"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredCinemas,
                        key = { it.cinema.id }
                    ) { item ->
                        CinemaCard(
                            cinemaWithDistance = item,
                            onCardClick = { viewModel.openDetail(item) },
                            onBookClick = {
                                NaverLinkHandler.openNaverUrl(context, item.cinema.naverUrl)
                            },
                            onMapClick = {
                                NaverLinkHandler.openNaverMap(
                                    context,
                                    item.cinema.name,
                                    item.cinema.latitude,
                                    item.cinema.longitude
                                )
                            },
                            onToggleFavorite = {
                                viewModel.toggleFavorite(item.cinema.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Detail Modal Sheet
    CinemaDetailSheet(
        cinemaWithDistance = selectedCinemaDetail,
        onDismiss = { viewModel.closeDetail() },
        onToggleFavorite = { viewModel.toggleFavorite(it) }
    )

    // Location Picker Sheet
    LocationPickerSheet(
        isOpen = isLocationPickerOpen,
        currentLocation = userLocation,
        isGpsLoading = isGpsLoading,
        onSelectLocation = { viewModel.setUserLocation(it) },
        onRequestGps = { handleGpsRequest() },
        onDismiss = { viewModel.setLocationPickerOpen(false) }
    )
}
