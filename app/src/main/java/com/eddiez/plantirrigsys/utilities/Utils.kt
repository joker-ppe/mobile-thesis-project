package com.eddiez.plantirrigsys.utilities

import android.graphics.Bitmap

object Utils {
    fun resizeImage(source: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        if (maxHeight > 0 && maxWidth > 0) {
            val ratioBitmap = source.width.toFloat() / source.height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            return Bitmap.createScaledBitmap(source, finalWidth, finalHeight, true)
        } else {
            return source
        }
    }
}