package com.sanitova.sanitovacheck

import android.content.Context

/**
 * Tracks how many free PDF exports a non-Pro user has used, lifetime
 * (no reset window — this is a one-time taste of the feature, not a
 * recurring allowance like scans). Pro users bypass this entirely.
 */
object PdfExportTracker {
    private const val PREFS_NAME = "sanitovacheck_prefs"
    private const val KEY_COUNT = "free_pdf_export_count"
    const val FREE_EXPORT_LIMIT = 2

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getCount(context: Context): Int {
        return prefs(context).getInt(KEY_COUNT, 0)
    }

    fun increment(context: Context) {
        val p = prefs(context)
        p.edit().putInt(KEY_COUNT, getCount(context) + 1).apply()
    }

    fun remaining(context: Context): Int {
        return (FREE_EXPORT_LIMIT - getCount(context)).coerceAtLeast(0)
    }

    fun hasReachedLimit(context: Context): Boolean {
        return getCount(context) >= FREE_EXPORT_LIMIT
    }
}
