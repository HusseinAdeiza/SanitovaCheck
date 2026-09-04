package com.sanitova.sanitovacheck

import android.content.Context

/**
 * Tracks free-tier scan usage within a rolling time window.
 * Free users get FREE_SCAN_LIMIT scans per WINDOW_HOURS, then must
 * wait for the window to reset (or upgrade to Pro, which bypasses
 * this entirely).
 */
object FreeScanTracker {
    private const val PREFS_NAME = "sanitovacheck_prefs"
    private const val KEY_COUNT = "free_scan_count"
    private const val KEY_WINDOW_START = "free_scan_window_start"

    const val FREE_SCAN_LIMIT = 5
    const val WINDOW_HOURS = 5
    const val FREE_HISTORY_LIMIT = 10
    private const val WINDOW_MILLIS = WINDOW_HOURS * 60 * 60 * 1000L

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Resets the count if the current window has expired. */
    private fun maybeResetWindow(context: Context) {
        val p = prefs(context)
        val windowStart = p.getLong(KEY_WINDOW_START, 0L)
        val now = System.currentTimeMillis()

        if (windowStart == 0L) {
            // First time ever — start the window now.
            p.edit().putLong(KEY_WINDOW_START, now).apply()
        } else if (now - windowStart >= WINDOW_MILLIS) {
            // Window expired — reset count and start a fresh window.
            p.edit()
                .putInt(KEY_COUNT, 0)
                .putLong(KEY_WINDOW_START, now)
                .apply()
        }
    }

    fun getCount(context: Context): Int {
        maybeResetWindow(context)
        return prefs(context).getInt(KEY_COUNT, 0)
    }

    fun increment(context: Context) {
        maybeResetWindow(context)
        val p = prefs(context)
        p.edit().putInt(KEY_COUNT, getCount(context) + 1).apply()
    }

    fun remaining(context: Context): Int {
        return (FREE_SCAN_LIMIT - getCount(context)).coerceAtLeast(0)
    }

    fun hasReachedLimit(context: Context): Boolean {
        return getCount(context) >= FREE_SCAN_LIMIT
    }

    /** Milliseconds remaining until the current window resets. */
    fun timeUntilResetMillis(context: Context): Long {
        maybeResetWindow(context)
        val windowStart = prefs(context).getLong(KEY_WINDOW_START, System.currentTimeMillis())
        val elapsed = System.currentTimeMillis() - windowStart
        return (WINDOW_MILLIS - elapsed).coerceAtLeast(0)
    }

    /** Human-readable countdown, e.g. "2h 14m" or "43m". */
    fun formattedTimeUntilReset(context: Context): String {
        val millis = timeUntilResetMillis(context)
        val hours = millis / (60 * 60 * 1000)
        val minutes = (millis % (60 * 60 * 1000)) / (60 * 1000)
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }
}
