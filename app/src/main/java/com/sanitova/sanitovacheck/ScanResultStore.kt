package com.sanitova.sanitovacheck

import android.net.Uri
import androidx.compose.runtime.mutableStateOf

/**
 * Simple in-memory holder for the most recent scan's photo + AI assessment.
 * Good enough for a single-scan-at-a-time flow. Once you add real scan
 * History (Pro feature), this should be replaced with a proper local
 * database (Room) keyed by scan/case ID.
 */
object ScanResultStore {
    var lastPhotoUris = mutableStateOf<List<Uri>>(emptyList())
    var lastResult = mutableStateOf<AssessResponse?>(null)
    var lastError = mutableStateOf<String?>(null)
}
