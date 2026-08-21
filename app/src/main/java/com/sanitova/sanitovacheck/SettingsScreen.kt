package com.sanitova.sanitovacheck

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sanitova.sanitovacheck.ui.theme.*

@Composable
fun SettingsScreen(onRequirePro: () -> Unit) {
    val context = LocalContext.current
    val hasProAccess by SubscriptionRepository.hasProAccess.collectAsState()

    LaunchedEffect(Unit) {
        SubscriptionRepository.refreshEntitlementStatus()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        if (!hasProAccess) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "SanitovaCheck Pro",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${FreeScanTracker.remaining(context)} of ${FreeScanTracker.FREE_SCAN_LIMIT} free scans remaining · resets every ${FreeScanTracker.WINDOW_HOURS}h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(12.dp))
                    ProBenefitsList()
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onRequirePro,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upgrade to Pro")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        SettingsRow(
            icon = Icons.Filled.CreditCard,
            label = "Manage Subscription",
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/account/subscriptions")
                )
                context.startActivity(intent)
            }
        )
        SettingsRow(
            icon = Icons.Filled.Refresh,
            label = "Restore Purchases",
            onClick = { SubscriptionRepository.refreshEntitlementStatus() }
        )
        SettingsRow(
            icon = Icons.Filled.Description,
            label = "Terms of Service",
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://sanitovaehs.com/terms.html"))
                )
            }
        )
        SettingsRow(
            icon = Icons.Filled.PrivacyTip,
            label = "Privacy Policy",
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://sanitovaehs.com/privacy.html"))
                )
            }
        )

        Spacer(Modifier.height(24.dp))
        Text(
            "SanitovaCheck by Sanitova Environmental Health Services Ltd",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
}
