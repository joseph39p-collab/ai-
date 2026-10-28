package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CinemaWithDistance
import com.example.model.SpecialScreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.util.DistanceCalculator
import com.example.util.NaverLinkHandler

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CinemaDetailSheet(
    cinemaWithDistance: CinemaWithDistance?,
    onDismiss: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cinemaWithDistance == null) return

    val cinema = cinemaWithDistance.cinema
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val formattedDist = DistanceCalculator.formatDistance(cinemaWithDistance.distanceKm)
    val travelEstimate = DistanceCalculator.estimateTravelTime(cinemaWithDistance.distanceKm)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("cinema_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Sheet Header with Close & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand pill
                Surface(
                    color = cinema.brand.brandColor.copy(alpha = 0.15f),
                    contentColor = cinema.brand.brandColor,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cinema.brand.brandColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${cinema.brand.iconEmoji} ${cinema.brand.displayName}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleFavorite(cinema.id) },
                        modifier = Modifier.testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (cinemaWithDistance.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "즐겨찾기",
                            tint = if (cinemaWithDistance.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cinema Name
            Text(
                text = cinema.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Distance & Travel Time Box
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("거리", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formattedDist, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkBorder))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("예상 이동", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(travelEstimate, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkBorder))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Theaters,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("규모", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${cinema.screenCount}관 / ${cinema.seatCount}석", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Special Screens & Characteristics Section
            Text(
                text = "상영관 구성 및 특징",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (cinema.isSmallOrStandardOnly || cinema.specialScreens.isEmpty()) {
                    Surface(
                        color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "☕ 소규모·일반 상영관",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B)
                                    )
                                )
                                Text(
                                    text = "특수관 없이 영화 본연의 스토리에 집중할 수 있는 편안하고 아늑한 일반 디지털 상영관입니다.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                cinema.specialScreens.forEach { screen ->
                    Surface(
                        color = screen.color.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, screen.color.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(screen.color)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = screen.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = screen.color
                                    )
                                )
                                Text(
                                    text = screen.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            if (cinema.description.isNotEmpty()) {
                Text(
                    text = "영화관 안내",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = cinema.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Address & Copy
            Text(
                text = "위치 및 연락처",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cinema.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("영화관 주소", cinema.address)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "주소가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "주소 복사",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "대표 번호: ${cinema.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = { NaverLinkHandler.openDialer(context, cinema.phone) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("전화 걸기", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Outbound Naver Actions
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        NaverLinkHandler.openNaverUrl(context, cinema.naverUrl)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("detail_naver_booking_cta"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF03C75A), // Naver Green
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "네이버 영화관 예매 / 상영시간표 보기",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = {
                        NaverLinkHandler.openNaverMap(context, cinema.name, cinema.latitude, cinema.longitude)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("detail_naver_map_cta"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "네이버 지도로 길찾기",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
