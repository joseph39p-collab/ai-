package com.example.model

data class UserLocation(
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val isGps: Boolean = false
) {
    companion object {
        val SEOUL_GANGNAM = UserLocation("서울 강남역", 37.4979, 127.0276)
        val SEOUL_YONGSAN = UserLocation("서울 용산역", 37.5298, 126.9647)
        val SEOUL_HONGDAE = UserLocation("서울 홍대입구", 37.5575, 126.9244)
        val GYEONGGI_PANGYO = UserLocation("경기 판교역", 37.3947, 127.1111)
        val BUSAN_SEOMYEON = UserLocation("부산 서면역", 35.1578, 129.0593)
        val DAEGU_DONGSEONGRO = UserLocation("대구 동성로", 35.8687, 128.5986)
        val DAEJEON_DUNSAN = UserLocation("대전 둔산", 36.3504, 127.3845)

        val PRESETS = listOf(
            SEOUL_GANGNAM,
            SEOUL_YONGSAN,
            SEOUL_HONGDAE,
            GYEONGGI_PANGYO,
            BUSAN_SEOMYEON,
            DAEGU_DONGSEONGRO,
            DAEJEON_DUNSAN
        )
    }
}

enum class SortMode(val displayName: String) {
    DISTANCE_ASC("가까운 순 (GPS)"),
    NAME_ASC("이름 순"),
    SPECIAL_COUNT_DESC("특수관 많은 순")
}

data class FilterState(
    val searchQuery: String = "",
    val selectedBrands: Set<CinemaBrand> = emptySet(),
    val selectedSpecialScreens: Set<SpecialScreen> = emptySet(),
    val onlySmallOrStandard: Boolean = false,
    val sortMode: SortMode = SortMode.DISTANCE_ASC,
    val favoritesOnly: Boolean = false
) {
    val isFilterActive: Boolean
        get() = searchQuery.isNotBlank() ||
                selectedBrands.isNotEmpty() ||
                selectedSpecialScreens.isNotEmpty() ||
                onlySmallOrStandard ||
                favoritesOnly ||
                sortMode != SortMode.DISTANCE_ASC
}

data class CinemaWithDistance(
    val cinema: Cinema,
    val distanceKm: Double,
    val isFavorite: Boolean = false
)
