package com.sanitova.sanitovacheck

import androidx.compose.runtime.*
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions

/**
 * Shown whenever a free-tier user taps a Pro-gated action
 * (Export PDF, view full History, etc).
 */
@Composable
fun PaywallScreen(onDismiss: () -> Unit) {
    val options = PaywallOptions.Builder(
        dismissRequest = { onDismiss() }
    ).build()

    Paywall(options = options)
}
