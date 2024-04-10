package com.eddiez.plantirrigsys.utilities

import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.eddiez.plantirrigsys.dataModel.ChatMessageDataModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.UUID
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

    fun bitmapToUri(bitmap: Bitmap, context: Context): Uri? {
        // Get the external storage directory
        val filesDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        // Create a file to save the bitmap
        val imageFile = File(filesDir, "image_" + UUID.randomUUID().toString() + ".png")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imageFile)
            // Use the compress method on the Bitmap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        // Use the FileProvider method to get the Uri of the file
        return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
    }

    fun parseHistoryChat(history: List<List<Any>>): MutableList<ChatMessageDataModel> {
        // Parse data
        val result = mutableListOf<ChatMessageDataModel>()
        for (item in history) {
            result.add(ChatMessageDataModel(item[0].toString(), true))
            result.add(ChatMessageDataModel(item[1].toString(), false))
        }
        return result
    }
}