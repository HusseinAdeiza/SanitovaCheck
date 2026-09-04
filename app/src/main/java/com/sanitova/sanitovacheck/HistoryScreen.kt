package com.sanitova.sanitovacheck

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.sanitova.sanitovacheck.ui.theme.*

@Composable
fun HistoryScreen(onRequirePro: () -> Unit, onOpenRecord: (String) -> Unit) {
    val context = LocalContext.current
    val hasProAccess by SubscriptionRepository.hasProAccess.collectAsState()
    var records by remember { mutableStateOf(listOf<ScanRecord>()) }

    LaunchedEffect(Unit) {
        SubscriptionRepository.refreshEntitlementStatus()
        records = ScanHistoryStore.getAll(context)
    }

    val displayRecords = if (hasProAccess) records else records.take(FreeScanTracker.FREE_HISTORY_LIMIT)
    val isAtFreeLimit = !hasProAccess && records.size >= FreeScanTracker.FREE_HISTORY_LIMIT

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars).padding(24.dp)) {
        Text("Scan History", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        if (!hasProAccess) {
            Text(
                "Free tier: ${displayRecords.size} of ${FreeScanTracker.FREE_HISTORY_LIMIT} saved scans shown",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }

        if (displayRecords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.History,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Your completed assessments will appear here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(displayRecords) { record ->
                    HistoryRow(record = record, onClick = { onOpenRecord(record.caseId) })
                }

                if (isAtFreeLimit) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "You've reached the free history limit.",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Upgrade to Pro for unlimited scan history and more features.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = onRequirePro, shape = RoundedCornerShape(14.dp)) {
                                    Text("Upgrade to Pro")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(record: ScanRecord, onClick: () -> Unit) {
    val riskColor = when (record.result.assessment.riskLevel.lowercase()) {
        "high" -> RiskHigh
        "medium" -> RiskMedium
        else -> RiskLow
    }
    val riskBg = when (record.result.assessment.riskLevel.lowercase()) {
        "high" -> RiskHighBg
        "medium" -> RiskMediumBg
        else -> RiskLowBg
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(riskBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${record.result.assessment.riskScore}",
                    style = MaterialTheme.typography.titleMedium,
                    color = riskColor
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    record.location,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date(record.timestampMillis)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
