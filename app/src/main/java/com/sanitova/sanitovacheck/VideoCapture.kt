package com.sanitova.sanitovacheck

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val MAX_RECORDING_SECONDS = 12

/**
 * Full-screen video preview with a record/stop button. Records a short, silent
 * clip (no audio track — the backend only reasons over extracted frames, so
 * audio would just add permission scope and file size for no benefit) and
 * returns its Uri via onVideoCaptured once recording finishes.
 */
@Composable
fun VideoCapture(
    onVideoCaptured: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val recorder = Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.SD))
                        .build()
                    val capture = VideoCapture.withOutput(recorder)
                    videoCapture = capture

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )
                    } catch (e: Exception) {
                        // TODO: surface a user-facing error if camera binding fails
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        // Top bar: cancel button. Preview stays edge-to-edge; only the control
        // itself is pushed clear of the status bar / camera cutout.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                if (isRecording) stopRecording()
                onCancel()
            }) {
                Text("✕", color = Color.White)
            }
        }

        if (isRecording) {
            Text(
                "REC · ${elapsedSeconds}s / ${MAX_RECORDING_SECONDS}s",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 16.dp)
            )
        }

        // Bottom bar: record/stop button. Kept clear of the nav bar, preview stays full-bleed.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onClick@{
                    val capture = videoCapture ?: return@onClick
                    if (isRecording) {
                        stopRecording()
                        return@onClick
                    }

                    val videoFile = File(
                        context.cacheDir,
                        "scan_video_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(java.util.Date())}.mp4"
                    )
                    val outputOptions = FileOutputOptions.Builder(videoFile).build()

                    isRecording = true
                    elapsedSeconds = 0

                    recording = capture.output
                        .prepareRecording(context, outputOptions)
                        .start(ContextCompat.getMainExecutor(context)) { event ->
                            if (event is VideoRecordEvent.Finalize) {
                                isRecording = false
                                if (!event.hasError()) {
                                    onVideoCaptured(Uri.fromFile(videoFile))
                                }
                            }
                        }

                    scope.launch {
                        while (isRecording && elapsedSeconds < MAX_RECORDING_SECONDS) {
                            delay(1000)
                            elapsedSeconds++
                        }
                        if (isRecording) stopRecording()
                    }
                }
            ) {
                Text(if (isRecording) "Stop Recording" else "Record Video (max ${MAX_RECORDING_SECONDS}s)")
            }
        }
    }
}
