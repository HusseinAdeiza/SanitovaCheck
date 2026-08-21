package com.sanitova.sanitovacheck

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Decodes an image URI to a base64 JPEG, downsampled to fit within [maxDimension]
 * on its longest side. Uses BitmapFactory.Options.inSampleSize so the full-resolution
 * image is never fully decoded into memory — a bounds-only pass reads the dimensions
 * first, then the real decode requests the nearest power-of-two downsample.
 *
 * Returns null if the image can't be read or decoded.
 */
fun downsampledBase64FromUri(context: Context, uri: Uri, maxDimension: Int = 1024): String? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        } ?: return null

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxDimension)
        }
        val sampledBitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null

        val scale = minOf(
            maxDimension.toFloat() / sampledBitmap.width,
            maxDimension.toFloat() / sampledBitmap.height,
            1f
        )
        val finalBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                sampledBitmap,
                (sampledBitmap.width * scale).toInt(),
                (sampledBitmap.height * scale).toInt(),
                true
            )
        } else {
            sampledBitmap
        }

        val outputStream = ByteArrayOutputStream()
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}

private fun calculateInSampleSize(width: Int, height: Int, reqDimension: Int): Int {
    var inSampleSize = 1
    if (height > reqDimension || width > reqDimension) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while ((halfHeight / inSampleSize) >= reqDimension && (halfWidth / inSampleSize) >= reqDimension) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
