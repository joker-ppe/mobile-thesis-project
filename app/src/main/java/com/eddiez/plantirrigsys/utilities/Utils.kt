package com.eddiez.plantirrigsys.utilities

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import java.util.Locale

object Utils {
    fun resizeImage(source: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        return if (maxHeight > 0 && maxWidth > 0) {
            val ratioBitmap = source.width.toFloat() / source.height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            Bitmap.createScaledBitmap(source, finalWidth, finalHeight, true)
        } else {
            source
        }
    }

    fun convertAndFormatDate(dateString: String): String? {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        originalFormat.timeZone =
            TimeZone.getTimeZone("UTC") // Adjust this if your original date is not in UTC
        val date = originalFormat.parse(dateString)

        val desiredFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        desiredFormat.timeZone = TimeZone.getDefault() // Device's default timezone

        return date?.let { desiredFormat.format(it) }
    }

    fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }
}