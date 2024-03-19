package com.eddiez.plantirrigsys.utilities

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.abs

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

    fun formatDateToShortMonthStyle(date: LocalDate): String {
        val format = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
        return format.format(date)
    }

    fun convertDate(dateString: String): LocalDate? {
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return try {
            LocalDate.parse(dateString, format)
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }


    fun convertStringToTime(timeString: String): LocalTime? {
        val format = DateTimeFormatter.ofPattern("HH:mm")
        return try {
            LocalTime.parse(timeString, format)
        } catch (e: DateTimeParseException) {
            e.printStackTrace()

            try {
                val format = DateTimeFormatter.ofPattern("H:mm")
                LocalTime.parse(timeString, format)
            } catch (e: DateTimeParseException) {
                e.printStackTrace()
                null
            }

        }
    }

    fun calculateTotalSeconds(localTime1: LocalTime, localTime2: LocalTime): Long {
        val duration = Duration.between(localTime1, localTime2)
        return abs(duration.seconds)
    }
}