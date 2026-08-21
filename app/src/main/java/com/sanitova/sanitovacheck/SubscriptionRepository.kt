package com.sanitova.sanitovacheck

import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.CustomerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Central place the rest of the app asks: "does this user have Pro access?"
 * Wraps RevenueCat's entitlement check and exposes it as observable state.
 */
object SubscriptionRepository {

    private const val PRO_ENTITLEMENT_ID = "SanitovaCheck Pro"

    private val _hasProAccess = MutableStateFlow(false)
    val hasProAccess: StateFlow<Boolean> = _hasProAccess

    fun refreshEntitlementStatus() {
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
    }
}
