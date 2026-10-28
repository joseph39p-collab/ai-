package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CinemaBrand
import com.example.model.FilterState
import com.example.model.SortMode
import com.example.model.SpecialScreen
import com.example.ui.theme.CineDolbyCyan
import com.example.ui.theme.CineGold
import com.example.ui.theme.CineOrange
import com.example.ui.theme.CineRed

@Composable
fun FilterChipsBar(
    filterState: FilterState,
    onToggleBrand: (CinemaBrand) -> Unit,
    onToggleSpecialScreen: (SpecialScreen) -> Unit,
    onToggleOnlySmallOrStandard: () -> Unit,
    onToggleSortNearest: () -> Unit,
    onToggleFavorites: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset chip (when filter active)
        if (filterState.isFilterActive) {
            SuggestionChip(
                onClick = onResetFilters,
                label = { Text("초기화", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "필터 초기화",
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = CineRed.copy(alpha = 0.15f),
                    labelColor = CineRed,
                    iconContentColor = CineRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CineRed.copy(alpha = 0.5f)),
                modifier = Modifier.testTag("filter_reset_chip")
            )
        }

        // 1. Distance Sort Chip (📍 내 주변)
        val isNearestSorted = filterState.sortMode == SortMode.DISTANCE_ASC
        FilterChip(
            selected = isNearestSorted,
            onClick = onToggleSortNearest,
            label = {
                Text(
                    text = "📍 내 주변",
                    fontSize = 12.sp,
                    fontWeight = if (isNearestSorted) FontWeight.Bold else FontWeight.Normal
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                selectedLabelColor = Color(0xFF34D399)
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isNearestSorted,
                borderColor = MaterialTheme.colorScheme.outline,
                selectedBorderColor = Color(0xFF10B981)
            ),
            modifier = Modifier.testTag("chip_nearest")
        )

        // 2. Small / Standard Screen Only Filter Chip (☕ 소규모·일반관)
        FilterChip(
            selected = filterState.onlySmallOrStandard,
            onClick = onToggleOnlySmallOrStandard,
            label = {
                Text(
                    text = "☕ 소규모·일반관",
                    fontSize = 12.sp,
                    fontWeight = if (filterState.onlySmallOrStandard) FontWeight.Bold else FontWeight.Normal
                )
            },
            leadingIcon = if (filterState.onlySmallOrStandard) {
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else null,
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFF59E0B).copy(alpha = 0.2f),
                selectedLabelColor = Color(0xFFF59E0B)
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = filterState.onlySmallOrStandard,
                borderColor = MaterialTheme.colorScheme.outline,
                selectedBorderColor = Color(0xFFF59E0B)
            ),
            modifier = Modifier.testTag("chip_small_standard")
        )

        // 3. Brand Chips
        CinemaBrand.entries.forEach { brand ->
            val isSelected = filterState.selectedBrands.contains(brand)
            FilterChip(
                selected = isSelected,
                onClick = { onToggleBrand(brand) },
                label = {
                    Text(
                        text = "${brand.iconEmoji} ${brand.displayName}",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else null,
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = brand.brandColor.copy(alpha = 0.2f),
                    selectedLabelColor = brand.brandColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = brand.brandColor
                ),
                modifier = Modifier.testTag("chip_brand_${brand.name}")
            )
        }

        // 4. Special Screen Chips (Strict AND condition)
        SpecialScreen.entries.forEach { screen ->
            val isSelected = filterState.selectedSpecialScreens.contains(screen)
            val iconEmoji = when (screen) {
                SpecialScreen.IMAX -> "🌟"
                SpecialScreen.FOUR_D -> "💨"
                SpecialScreen.DOLBY -> "🔊"
                SpecialScreen.SCREEN_X -> "📐"
                SpecialScreen.RECLINER -> "🛋️"
            }
            FilterChip(
                selected = isSelected,
                onClick = { onToggleSpecialScreen(screen) },
                label = {
                    Text(
                        text = "$iconEmoji ${screen.shortName}",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else null,
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = screen.color.copy(alpha = 0.2f),
                    selectedLabelColor = screen.color
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = screen.color
                ),
                modifier = Modifier.testTag("chip_screen_${screen.name}")
            )
        }

        // 4. Favorites Only
        FilterChip(
            selected = filterState.favoritesOnly,
            onClick = onToggleFavorites,
            label = {
                Text(
                    text = "❤️ 즐겨찾기",
                    fontSize = 12.sp,
                    fontWeight = if (filterState.favoritesOnly) FontWeight.Bold else FontWeight.Normal
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                selectedLabelColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.testTag("chip_favorites")
        )
    }
}
