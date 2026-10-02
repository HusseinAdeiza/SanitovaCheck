package com.sanitova.sanitovacheck

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Central place the rest of the app asks: "does this user have Pro access?"
 * Wraps RevenueCat's entitlement check and exposes it as observable state.
 */
object SubscriptionRepository {

    private const val PRO_ENTITLEMENT_ID = "SanitovaCheck Pro"

    /** RevenueCat must be configured before sharedInstance is safe to touch. */
    private val configurationLatch = CountDownLatch(1)

    private val _hasProAccess = MutableStateFlow(false)
    val hasProAccess: StateFlow<Boolean> = _hasProAccess

    fun markConfigured() {
        configurationLatch.countDown()
    }

    /**
     * Blocks until RevenueCat has been configured. Returns false if it never
     * completes, so callers degrade to no-access rather than hanging or crashing.
     */
    fun awaitConfiguration(timeoutSeconds: Long = 10): Boolean {
        return try {
            configurationLatch.await(timeoutSeconds, TimeUnit.SECONDS)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            false
        }
    }

    fun refreshEntitlementStatus() {
        // Purchases.sharedInstance throws IllegalStateException if configure()
        // hasn't finished. Without this guard, opening Report/History/Settings
        // immediately after launch could crash the app.
        if (!awaitConfiguration()) {
            _hasProAccess.value = false
            return
        }

        try {
            Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    _hasProAccess.value =
                        customerInfo.entitlements.active.containsKey(PRO_ENTITLEMENT_ID)
                }

                override fun onError(error: PurchasesError) {
                    // Fail safe: treat as no access rather than crashing the app.
                    _hasProAccess.value = false
                }
            })
        } catch (_: Exception) {
            // Purchases unavailable for any reason — degrade to free tier.
            _hasProAccess.value = false
        }
    }

    /**
     * Re-associates the RevenueCat customer with the signed-in Firebase user so
     * a purchase made while signed out still restores after sign-in.
     */
    fun syncUserIdentity(firebaseUid: String?) {
        if (firebaseUid == null) return
        if (!awaitConfiguration()) return
        try {
            Purchases.sharedInstance.logIn(firebaseUid)
        } catch (_: Exception) {
            // Non-fatal — anonymous access still works.
        }
    }
}
