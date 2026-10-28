package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object NaverLinkHandler {

    /**
     * Opens the theater's Naver reservation / search results page in the system browser
     * or supported browser app.
     */
    fun openNaverUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "브라우저를 열 수 없습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the cinema in Naver Map for navigation or directions.
     * Attempts the Naver Map app first, falling back to mobile web.
     */
    fun openNaverMap(context: Context, theaterName: String, lat: Double, lon: Double) {
        try {
            val encodedName = URLEncoder.encode(theaterName, "UTF-8")
            val appSchemeUri = Uri.parse(
                "nmap://place?lat=$lat&lng=$lon&name=$encodedName&appname=${context.packageName}"
            )
            val mapIntent = Intent(Intent.ACTION_VIEW, appSchemeUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            // Fallback to Naver Map mobile web
            val encodedName = URLEncoder.encode(theaterName, "UTF-8")
            val webUri = "https://m.map.naver.com/search2/search.naver?query=$encodedName"
            openNaverUrl(context, webUri)
        }
    }

    /**
     * Opens the system dialer with the cinema phone number.
     */
    fun openDialer(context: Context, phoneNumber: String) {
        try {
            val cleanPhone = phoneNumber.replace("-", "").trim()
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "전화 앱을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
