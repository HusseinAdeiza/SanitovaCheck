package com.sanitova.sanitovacheck

import android.content.Context
import java.util.Calendar
import java.util.TimeZone

/**
 * Tracks video recording usage for free vs Pro tiers.
 *
 * Free users: 1 lifetime video trial. Once used, they must upgrade to Pro.
 * Pro users: 10 videos per day with daily reset at midnight.
 */
object VideoTrialTracker {
    private const val PREFS_NAME = "sanitovacheck_prefs"
    private const val KEY_FREE_TRIAL_USED = "video_free_trial_used"
    private const val KEY_PRO_DAILY_COUNT = "video_pro_daily_count"
    private const val KEY_PRO_DAILY_DATE = "video_pro_daily_date"

    const val PRO_DAILY_LIMIT = 10

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Returns true if the user (free or Pro) can record a video right now. */
    fun canRecordVideo(context: Context, hasPro: Boolean): Boolean {
        return if (hasPro) {
            remainingProVideos(context) > 0
        } else {
            !hasUsedFreeTrial(context)
        }
    }

    /** Call this after a video is successfully recorded/submitted. */
    fun recordVideo(context: Context, hasPro: Boolean) {
        val p = prefs(context)
        if (hasPro) {
            maybeResetProDay(context)
            val current = p.getInt(KEY_PRO_DAILY_COUNT, 0)
            p.edit().putInt(KEY_PRO_DAILY_COUNT, current + 1).apply()
        } else {
            p.edit().putBoolean(KEY_FREE_TRIAL_USED, true).apply()
        }
    }

    /** Free users only: has their one-time trial been used? */
    fun hasUsedFreeTrial(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_FREE_TRIAL_USED, false)
    }

    /** Pro users only: how many videos remain today? */
    fun remainingProVideos(context: Context): Int {
        maybeResetProDay(context)
        val used = prefs(context).getInt(KEY_PRO_DAILY_COUNT, 0)
        return (PRO_DAILY_LIMIT - used).coerceAtLeast(0)
    }

    /** Pro users only: human-readable status like "3/10 today". */
    fun proStatusText(context: Context): String {
        val remaining = remainingProVideos(context)
        return "$remaining/$PRO_DAILY_LIMIT today"
    }

    /** Resets the Pro daily count if the stored date is not today. */
    private fun maybeResetProDay(context: Context) {
        val p = prefs(context)
        val storedDate = p.getLong(KEY_PRO_DAILY_DATE, 0L)
        val today = currentDayEpochMillis()

        if (storedDate != today) {
            p.edit()
                .putInt(KEY_PRO_DAILY_COUNT, 0)
                .putLong(KEY_PRO_DAILY_DATE, today)
                .apply()
        }
    }

    /** Midnight of today in UTC millis. */
    private fun currentDayEpochMillis(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
