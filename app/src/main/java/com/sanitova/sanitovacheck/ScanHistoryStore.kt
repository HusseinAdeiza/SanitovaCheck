package com.sanitova.sanitovacheck

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A single saved assessment — everything needed to redisplay it later
 * in History or Report screens without re-calling the API.
 */
data class ScanRecord(
    val caseId: String,
    val timestampMillis: Long,
    val location: String,
    val description: String,
    val photoUriStrings: List<String> = emptyList(),
    val result: AssessResponse
)

/**
 * Persistent local storage for scan history. Uses SharedPreferences +
 * Gson JSON serialization — no database setup required, works fully
 * offline, survives app restarts.
 *
 * Note: this is per-device local storage only. If you add real user
 * accounts later, you'll want to sync this to a backend so history
 * follows the user across devices instead of staying local.
 */
object ScanHistoryStore {

    private const val PREFS_NAME = "sanitovacheck_history"
    private const val KEY_RECORDS = "records"

    private val gson = Gson()

    fun save(context: Context, record: ScanRecord) {
        val current = getAll(context).toMutableList()
        current.add(0, record) // newest first
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_RECORDS, gson.toJson(current)).apply()
    }

    fun getAll(context: Context): List<ScanRecord> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_RECORDS, null) ?: return emptyList()
        val type = object : TypeToken<List<ScanRecord>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList() // corrupted data — fail safe rather than crash
        }
    }

    fun getById(context: Context, caseId: String): ScanRecord? {
        return getAll(context).find { it.caseId == caseId }
    }
}
