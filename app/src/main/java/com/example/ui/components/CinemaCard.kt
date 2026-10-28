package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CinemaWithDistance
import com.example.model.SpecialScreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.util.DistanceCalculator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CinemaCard(
    cinemaWithDistance: CinemaWithDistance,
    onCardClick: () -> Unit,
    onBookClick: () -> Unit,
    onMapClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cinema = cinemaWithDistance.cinema
    val formattedDist = DistanceCalculator.formatDistance(cinemaWithDistance.distanceKm)
    val brand = cinema.brand

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cinema_card_${cinema.id}")
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Brand Tag + Distance Pill + Favorite Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Brand Badge
                    Surface(
                        color = brand.brandColor.copy(alpha = 0.15f),
                        contentColor = brand.brandColor,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, brand.brandColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${brand.iconEmoji} ${brand.displayName}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Distance badge
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        contentColor = Color(0xFF34D399),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = formattedDist,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Favorite Toggle
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("favorite_button_${cinema.id}")
                ) {
                    Icon(
                        imageVector = if (cinemaWithDistance.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "즐겨찾기 토글",
                        tint = if (cinemaWithDistance.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cinema Name & Branch
            Text(
                text = cinema.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Address snippet
            Text(
                text = cinema.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Badges (IMAX, 4DX, Dolby, ScreenX, Recliner, or Small/Standard)
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (cinema.isSmallOrStandardOnly || cinema.specialScreens.isEmpty()) {
                    Surface(
                        color = Color(0xFFF59E0B).copy(alpha = 0.16f),
                        contentColor = Color(0xFFF59E0B),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "☕ 소규모·일반관",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
                cinema.specialScreens.forEach { screen ->
                    SpecialScreenBadge(screen = screen)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Naver Booking CTA + Map Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary Naver Booking Button
                Button(
                    onClick = onBookClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("book_button_${cinema.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF03C75A), // Naver Green
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "네이버 예매",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Secondary Map Navigation Button
                OutlinedButton(
                    onClick = onMapClick,
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("map_button_${cinema.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "길찾기",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SpecialScreenBadge(
    screen: SpecialScreen,
    modifier: Modifier = Modifier
) {
    Surface(
        color = screen.color.copy(alpha = 0.16f),
        contentColor = screen.color,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, screen.color.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(screen.color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = screen.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
    }
}
