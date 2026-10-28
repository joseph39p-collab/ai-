package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BrandCgv
import com.example.ui.theme.BrandIndie
import com.example.ui.theme.BrandLotte
import com.example.ui.theme.BrandMegaboxLight
import com.example.ui.theme.CineDolbyCyan
import com.example.ui.theme.CineGold
import com.example.ui.theme.CineOrange

enum class CinemaBrand(
    val id: String,
    val displayName: String,
    val iconEmoji: String,
    val brandColor: Color
) {
    CGV("CGV", "CGV", "🍿", BrandCgv),
    MEGABOX("MEGABOX", "메가박스", "🍿", BrandMegaboxLight),
    LOTTE("LOTTE", "롯데시네마", "🍿", BrandLotte),
    INDIE("INDIE", "독립/기타", "🎬", BrandIndie);

    companion object {
        fun fromString(value: String): CinemaBrand? {
            return entries.find {
                it.name.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true) ||
                it.id.equals(value, ignoreCase = true)
            }
        }
    }
}

enum class SpecialScreen(
    val code: String,
    val displayName: String,
    val shortName: String,
    val color: Color,
    val description: String
) {
    IMAX(
        code = "IMAX",
        displayName = "IMAX (아이맥스)",
        shortName = "IMAX",
        color = CineGold,
        description = "세계 최대 스크린 & 차세대 레이저 영사 시스템"
    ),
    FOUR_D(
        code = "4D",
        displayName = "4DX / Super 4D",
        shortName = "4D",
        color = CineOrange,
        description = "모션 시트와 바람·물·향기 환경 특수효과"
    ),
    DOLBY(
        code = "DOLBY",
        displayName = "돌비 시네마 / Atmos",
        shortName = "돌비",
        color = CineDolbyCyan,
        description = "Dolby Vision HDR 컬러와 Dolby Atmos 3D 입체 음향"
    ),
    SCREEN_X(
        code = "SCREENX",
        displayName = "ScreenX (3면 스크린)",
        shortName = "ScreenX",
        color = Color(0xFFA78BFA),
        description = "정면과 좌우 벽면을 감싸는 270도 파노라마 화면"
    ),
    RECLINER(
        code = "RECLINER",
        displayName = "리클라이너 (편안한 전동좌석)",
        shortName = "리클라이너",
        color = Color(0xFF10B981),
        description = "발받침과 등받이 각도 조절이 가능한 최고급 리클라이너 좌석"
    )
}

data class Cinema(
    val id: String,
    val brand: CinemaBrand,
    val name: String,
    val branchName: String,
    val latitude: Double,
    val longitude: Double,
    val hasImax: Boolean = false,
    val has4d: Boolean = false,
    val hasDolby: Boolean = false,
    val hasScreenX: Boolean = false,
    val hasRecliner: Boolean = false,
    val isSmallOrStandardOnly: Boolean = false,
    val address: String,
    val region: String,
    val naverUrl: String,
    val naverMapUrl: String,
    val phone: String = "1544-0000",
    val screenCount: Int = 8,
    val seatCount: Int = 1200,
    val description: String = ""
) {
    val specialScreens: List<SpecialScreen>
        get() = buildList {
            if (hasImax) add(SpecialScreen.IMAX)
            if (has4d) add(SpecialScreen.FOUR_D)
            if (hasDolby) add(SpecialScreen.DOLBY)
            if (hasScreenX) add(SpecialScreen.SCREEN_X)
            if (hasRecliner) add(SpecialScreen.RECLINER)
        }
}
