package com.sanitova.sanitovacheck

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.LogLevel
import java.util.concurrent.Executors

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // RevenueCat config happens off the main thread, but every screen that
        // checks entitlement can run before it finishes. Callers wait on this
        // latch rather than touching Purchases.sharedInstance early, which throws
        // IllegalStateException when configure hasn't completed.
        SubscriptionRepository.awaitConfiguration()

        Executors.newSingleThreadExecutor().execute {
            try {
                Purchases.logLevel = LogLevel.WARN
                Purchases.configure(
                    PurchasesConfiguration.Builder(this, "goog_yTBQQRvCNispVLUsLEjgDZrqSfM")
                        .appUserID(FirebaseAuth.getInstance().currentUser?.uid)
                        .build()
                )
                // Tie the RevenueCat identity to the Firebase account so purchases
                // restore on sign-in instead of staying anonymous to one device.
                FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
                    runCatching { Purchases.sharedInstance.logIn(uid) }
                }
            } catch (_: Exception) {
                // Fail safe — entitlement checks degrade to no-access rather than
                // crashing the app.
            } finally {
                SubscriptionRepository.markConfigured()
            }
        }
    }
}
