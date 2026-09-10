package com.sanitova.sanitovacheck

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.LogLevel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Purchases.logLevel = LogLevel.WARN
        // Production API key, linked to the real Android app entry in RevenueCat.
        // Configure off the main thread to avoid any startup blocking.
        Thread {
            try {
                Purchases.configure(
                    PurchasesConfiguration.Builder(this, "goog_yTBQQRvCNispVLUsLEjgDZrqSfM")
                        .build()
                )
            } catch (_: Exception) {
                // Fail silently — subscription checks will just return no access
            }
        }.start()
    }
}
