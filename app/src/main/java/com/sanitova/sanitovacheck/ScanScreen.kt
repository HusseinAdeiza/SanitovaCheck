package com.sanitova.sanitovacheck

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import com.sanitova.sanitovacheck.ui.theme.*

private enum class ScanStep { LIMIT_REACHED, PERMISSION, CAMERA, VIDEO, FORM, SUBMITTING }

@Composable
fun ScanScreen(onScanComplete: (String) -> Unit, onRequirePro: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hasProAccess by SubscriptionRepository.hasProAccess.collectAsState()

    LaunchedEffect(Unit) {
        SubscriptionRepository.refreshEntitlementStatus()
    }

    val canScan = hasProAccess || !FreeScanTracker.hasReachedLimit(context)
    val maxPhotos = if (hasProAccess) 10 else 2
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var videoFrameUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isExtractingFrames by remember { mutableStateOf(false) }

    var step by remember {
        mutableStateOf(
            if (!canScan) ScanStep.LIMIT_REACHED
            else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                ScanStep.CAMERA
            else ScanStep.PERMISSION
        )
    }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        step = if (granted) ScanStep.CAMERA else ScanStep.PERMISSION
    }

    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var submitError by remember { mutableStateOf<String?>(null) }

    fun submit() {
        step = ScanStep.SUBMITTING
        submitError = null
        val caseId = "SC-${UUID.randomUUID().toString().take(8)}"
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date())

        scope.launch {
            try {
                val allImageUris = photoUris + videoFrameUris
                val imagesBase64 = withContext(Dispatchers.IO) {
                    allImageUris.mapNotNull { uri -> downsampledBase64FromUri(context, uri) }
                }

                val response = WashApiClient.service.assess(
                    AssessRequest(
                        caseId = caseId,
                        location = location,
                        reportChannel = "photo",
                        incidentDescription = description,
                        reportedDate = today,
                        imagesBase64 = imagesBase64.ifEmpty { null }
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    ScanResultStore.lastResult.value = result
                    ScanResultStore.lastError.value = null

                    if (!hasProAccess) {
                        FreeScanTracker.increment(context)
                    }

                    val historyLimit = if (hasProAccess) null else 10
                    ScanHistoryStore.save(
                        context,
                        ScanRecord(
                            caseId = caseId,
                            timestampMillis = System.currentTimeMillis(),
                            location = location,
                            description = description,
                            photoUriStrings = allImageUris.map { it.toString() },
                            result = result
                        ),
                        maxRecords = historyLimit
                    )

                    onScanComplete(caseId)
                } else {
                    submitError = "Assessment failed (server responded with an error, code ${response.code()}). Please try again."
                    step = ScanStep.FORM
                }
            } catch (e: UnknownHostException) {
                submitError = "No internet connection. Check your network and try again."
                step = ScanStep.FORM
            } catch (e: SocketTimeoutException) {
                submitError = "The request timed out. Your connection may be slow — try again."
                step = ScanStep.FORM
            } catch (e: Exception) {
                submitError = "Something went wrong. Please try again."
                step = ScanStep.FORM
            }
        }
    }

    when (step) {
        ScanStep.LIMIT_REACHED -> {
            Column(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "You've used all ${FreeScanTracker.FREE_SCAN_LIMIT} free scans",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Resets in ${FreeScanTracker.formattedTimeUntilReset(context)} — or upgrade to Pro for unlimited scans anytime:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                ProBenefitsList()
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onRequirePro,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Upgrade to Pro")
                }
            }
        }

        ScanStep.PERMISSION -> {
            Column(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("SanitovaCheck needs camera access to photograph the facility you're assessing.")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Camera Access")
                }
            }
        }

        ScanStep.CAMERA -> {
            CameraCapture(
                onPhotoCaptured = { uri ->
                    photoUris = photoUris + uri
                    ScanResultStore.lastPhotoUris.value = photoUris
                    step = ScanStep.FORM
                },
                onCancel = { }
            )
        }

        ScanStep.VIDEO -> {
            VideoCapture(
                onVideoCaptured = { videoUri ->
                    VideoTrialTracker.recordVideo(context, hasProAccess)
                    isExtractingFrames = true
                    step = ScanStep.FORM
                    scope.launch {
                        val frames = withContext(Dispatchers.IO) {
                            extractVideoFrames(context, videoUri)
                        }
                        videoFrameUris = frames
                        ScanResultStore.lastPhotoUris.value = photoUris + videoFrameUris
                        isExtractingFrames = false
                    }
                },
                onCancel = { step = ScanStep.FORM }
            )
        }

        ScanStep.FORM, ScanStep.SUBMITTING -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(24.dp)
                ) {
                    Text("Describe what you found", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    if (!hasProAccess) {
                        val remaining = FreeScanTracker.remaining(context)
                        Text(
                            "$remaining free ${if (remaining == 1) "scan" else "scans"} remaining · resets every ${FreeScanTracker.WINDOW_HOURS}h",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Photos (${photoUris.size}/$maxPhotos)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        photoUris.forEachIndexed { index, uri ->
                            Box(modifier = Modifier.size(84.dp).padding(end = 8.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Captured photo ${index + 1}",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    enabled = step != ScanStep.SUBMITTING,
                                    onClick = {
                                        photoUris = photoUris.filterIndexed { i, _ -> i != index }
                                        ScanResultStore.lastPhotoUris.value = photoUris
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(22.dp)
                                        .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Remove photo ${index + 1}",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        if (photoUris.size < maxPhotos) {
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable(enabled = step != ScanStep.SUBMITTING) {
                                        step = ScanStep.CAMERA
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add another photo",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    if (!hasProAccess && photoUris.size >= maxPhotos) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Upgrade to Pro for up to 10 photos per scan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onRequirePro() }
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    when {
                        videoFrameUris.isNotEmpty() -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Videocam,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Video clip attached (${videoFrameUris.size} frames)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    enabled = step != ScanStep.SUBMITTING,
                                    onClick = {
                                        videoFrameUris = emptyList()
                                        ScanResultStore.lastPhotoUris.value = photoUris
                                    }
                                ) {
                                    Text("Remove")
                                }
                            }
                        }
                        isExtractingFrames -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Extracting video frames...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        else -> {
                            val canRecordVideo = VideoTrialTracker.canRecordVideo(context, hasProAccess)
                            val videoLabel = when {
                                hasProAccess -> "Add video clip (${VideoTrialTracker.proStatusText(context)})"
                                !VideoTrialTracker.hasUsedFreeTrial(context) -> "Add video clip — 1 free trial"
                                else -> "Add video clip (Pro)"
                            }
                            OutlinedButton(
                                enabled = step != ScanStep.SUBMITTING && canRecordVideo,
                                onClick = {
                                    if (canRecordVideo) step = ScanStep.VIDEO
                                    else onRequirePro()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(videoLabel)
                            }
                            if (!hasProAccess && VideoTrialTracker.hasUsedFreeTrial(context)) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "You've used your free video trial. Upgrade to Pro for daily video scans.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { onRequirePro() }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location (e.g. City, Country/Region)") },
                        enabled = step != ScanStep.SUBMITTING,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("What did you observe?") },
                        enabled = step != ScanStep.SUBMITTING,
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    submitError?.let { error ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RiskHighBg, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (error.contains("internet", ignoreCase = true)) Icons.Filled.WifiOff
                                else Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = RiskHigh,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(error, color = RiskHigh, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Button(
                        enabled = step != ScanStep.SUBMITTING && location.isNotBlank() && description.isNotBlank(),
                        onClick = { submit() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (submitError != null) "Retry" else "Submit for Assessment")
                    }
                }

                if (step == ScanStep.SUBMITTING) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Analyzing your submission...",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Checking your photo and description against WASH standards.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
