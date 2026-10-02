package com.sanitova.sanitovacheck

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Full-screen camera preview with a capture button.
 * On successful capture, returns the saved photo's Uri via onPhotoCaptured.
 */
@Composable
fun CameraCapture(
    onPhotoCaptured: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

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

                    val capture = ImageCapture.Builder().build()
                    imageCapture = capture

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
                        // Surface this — otherwise the user gets a black preview and a
                        // capture button that silently does nothing.
                        cameraError = when {
                            e is SecurityException ->
                                "Camera permission was denied. Enable it in Settings to take a photo."
                            e is IllegalArgumentException ->
                                "No rear camera is available on this device."
                            else ->
                                "Couldn't start the camera. Close other apps using it and try again."
                        }
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
            IconButton(onClick = onCancel) {
                Text("✕", color = androidx.compose.ui.graphics.Color.White)
            }
        }

        // Bottom bar: capture button. Kept clear of the nav bar, preview stays full-bleed.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                enabled = !isCapturing && cameraError == null && imageCapture != null,
                onClick = {
                    val capture = imageCapture ?: run {
                        cameraError = "Camera isn't ready yet. Wait a moment and try again."
                        return@Button
                    }
                    isCapturing = true

                    // Store outside cacheDir: history persists these URIs, and the
                    // system may reclaim cache at any time, leaving broken images
                    // in the History list.
                    val photoDir = File(
                        context.getExternalFilesDir(null) ?: context.filesDir,
                        "scans"
                    )
                    if (!photoDir.exists()) photoDir.mkdirs()
                    val photoFile = File(
                        photoDir,
                        "scan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(java.util.Date())}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                isCapturing = false
                                onPhotoCaptured(Uri.fromFile(photoFile))
                            }

                            override fun onError(exception: ImageCaptureException) {
                                isCapturing = false
                                cameraError = "The photo couldn't be taken. Please try again."
                            }
                        }
                    )
                }
            ) {
                Text(if (isCapturing) "Capturing..." else "Capture Photo")
            }

            // Surface camera problems instead of leaving a dead black screen.
            cameraError?.let { err ->
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(top = 56.dp, start = 24.dp, end = 24.dp)
                        .background(
                            MaterialTheme.colorScheme.error.copy(alpha = 0.92f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        err,
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { cameraError = null }) {
                        Text("Try again", color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
        }
    }
}
