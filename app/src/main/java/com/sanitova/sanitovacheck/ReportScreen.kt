package com.sanitova.sanitovacheck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sanitova.sanitovacheck.ui.theme.*

private data class RiskStyle(val color: Color, val bg: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private fun riskStyleFor(level: String): RiskStyle = when (level.lowercase()) {
    "high" -> RiskStyle(RiskHigh, RiskHighBg, Icons.Filled.Warning)
    "medium" -> RiskStyle(RiskMedium, RiskMediumBg, Icons.Filled.Info)
    else -> RiskStyle(RiskLow, RiskLowBg, Icons.Filled.CheckCircle)
}

@Composable
fun ReportScreen(scanId: String, onRequirePro: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hasProAccess by SubscriptionRepository.hasProAccess.collectAsState()
    val liveResult by ScanResultStore.lastResult
    val livePhotoUris by ScanResultStore.lastPhotoUris

    // If this scanId matches the just-completed live scan, use that directly
    // (fastest path, no disk read needed). Otherwise, this is a historical
    // scan being reopened from the History list — load it from storage.
    val isLiveMatch = liveResult?.caseId == scanId
    val historicalRecord = remember(scanId) {
        if (!isLiveMatch) ScanHistoryStore.getById(context, scanId) else null
    }

    val result = if (isLiveMatch) liveResult else historicalRecord?.result
    val photoUris = if (isLiveMatch) livePhotoUris
        else historicalRecord?.photoUriStrings?.map { android.net.Uri.parse(it) } ?: emptyList()

    LaunchedEffect(Unit) {
        SubscriptionRepository.refreshEntitlementStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Compliance Report", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(
            "Case ID: $scanId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        if (photoUris.isNotEmpty()) {
            if (photoUris.size == 1) {
                AsyncImage(
                    model = photoUris.first(),
                    contentDescription = "Photo captured during assessment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    photoUris.forEachIndexed { index, uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Photo ${index + 1} captured during assessment",
                            modifier = Modifier
                                .size(160.dp)
                                .padding(end = if (index < photoUris.lastIndex) 8.dp else 0.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Photo verification section — shown whenever the server returns
        // photo verification data, regardless of whether local photo URIs
        // are available.
        result?.photoVerification?.let { verification ->
            if (verification.matches != null || verification.aiObservedDescription != null) {
                val verifiedGood = verification.matches == true
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (verifiedGood) RiskLowBg else RiskHighBg
                        )
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (verifiedGood) Icons.Filled.VerifiedUser else Icons.Filled.Warning,
                            contentDescription = null,
                            tint = if (verifiedGood) RiskLow else RiskHigh,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                if (verifiedGood) "Photo matches description" else "⚠ PHOTO DOES NOT MATCH DESCRIPTION",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (verifiedGood) RiskLow else RiskHigh,
                                fontWeight = FontWeight.Bold
                            )
                            if (!verifiedGood) {
                                Text(
                                    "This assessment is UNVERIFIED. The photo does not appear to match what was described.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RiskHigh,
                                )
                            }
                        }
                    }

                    // Show what the AI independently saw
                    verification.aiObservedDescription?.let { observed ->
                        if (observed.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (verifiedGood) "What the AI observed:" else "What the AI actually observed (differs from description):",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (verifiedGood) MaterialTheme.colorScheme.onSurfaceVariant else RiskHigh,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                observed,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (verifiedGood) MaterialTheme.colorScheme.onSurfaceVariant else RiskHigh,
                            )
                        }
                    }

                    // Show explanation
                    verification.explanation?.let { expl ->
                        if (expl.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                expl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        if (result == null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    "No assessment data available for this scan.",
                    modifier = Modifier.padding(20.dp)
                )
            }
            return@Column
        }

        val assessment = result!!.assessment
        val routing = result!!.routing
        val style = riskStyleFor(assessment.riskLevel)
        val photoMismatched = result!!.photoVerification?.matches == false
        val hasPhotoVerification = result!!.photoVerification?.matches != null || result!!.photoVerification?.aiObservedDescription != null

        // Risk level hero card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = if (photoMismatched) RiskHighBg else style.bg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (photoMismatched) Icons.Filled.Warning else style.icon,
                        contentDescription = null,
                        tint = if (photoMismatched) RiskHigh else style.color,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (photoMismatched) "UNVERIFIED ASSESSMENT" else "${assessment.riskLevel.uppercase()} RISK",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (photoMismatched) RiskHigh else style.color,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(6.dp))
                if (photoMismatched) {
                    Text(
                        "The photo does not match the description. Assessment based on text only — not verified against photo evidence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RiskHigh
                    )
                } else {
                    Text(
                        "Risk Score: ${assessment.riskScore} / 10",
                        style = MaterialTheme.typography.titleMedium,
                        color = style.color
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        SectionCard(title = "Summary") {
            Text(assessment.summary, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(Modifier.height(16.dp))

        SectionCard(title = "Key Concerns") {
            assessment.keyConcerns.forEach { concern ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("•  ", color = MaterialTheme.colorScheme.primary)
                    Text(concern, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        SectionCard(title = "Recommended Action") {
            Text(
                assessment.recommendedAction.replace("_", " ").replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Assigned to: ${routing.assignTo.replace("_", " ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Response window: ${routing.slaHours} hours",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                val currentResult = result
                if (currentResult == null) {
                    // no-op, nothing to export
                } else if (hasProAccess || !PdfExportTracker.hasReachedLimit(context)) {
                    if (!hasProAccess) {
                        PdfExportTracker.increment(context)
                    }
                    val pdfUri = PdfReportGenerator.generate(context, scanId, currentResult)
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(android.content.Intent.EXTRA_STREAM, pdfUri)
                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(
                        android.content.Intent.createChooser(shareIntent, "Share Compliance Report")
                    )
                } else {
                    onRequirePro()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Export PDF Report", style = MaterialTheme.typography.titleMedium)
        }

        if (!hasProAccess) {
            Spacer(Modifier.height(8.dp))
            val remaining = PdfExportTracker.remaining(context)
            Text(
                if (remaining > 0)
                    "$remaining free PDF ${if (remaining == 1) "export" else "exports"} remaining"
                else
                    "No free exports remaining — upgrade to Pro for unlimited exports",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
