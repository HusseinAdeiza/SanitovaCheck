package com.sanitova.sanitovacheck

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.LogLevel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Purchases.logLevel = LogLevel.DEBUG
        // Production API key, linked to the real Android app entry in RevenueCat.
        Purchases.configure(
            PurchasesConfiguration.Builder(this, "goog_yTBQQRvCNispVLUsLEjgDZrqSfM")
                .build()
        )
    }
}