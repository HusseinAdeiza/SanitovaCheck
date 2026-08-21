package com.sanitova.sanitovacheck

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The single source of truth for what Pro actually includes.
 * Keep this list in sync with the RevenueCat paywall's feature list —
 * if you change one, change the other, so marketing copy and actual
 * app behavior never drift apart again (like the "unlimited scans"
 * mismatch we just fixed).
 */
val proBenefits = listOf(
    "Unlimited compliance scans (free tier: ${FreeScanTracker.FREE_SCAN_LIMIT} every ${FreeScanTracker.WINDOW_HOURS} hours)",
    "Unlimited PDF compliance certificates (free tier: ${PdfExportTracker.FREE_EXPORT_LIMIT} total)",
    "Full scan history & tracking",
    "Multi-location facility management",
    "Priority support"
)

@Composable
fun ProBenefitsList(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        proBenefits.forEach { benefit ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(benefit, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
