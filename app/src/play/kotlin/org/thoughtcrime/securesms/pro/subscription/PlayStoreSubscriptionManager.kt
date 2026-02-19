package org.thoughtcrime.securesms.pro.subscription

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.session.libsession.utilities.TextSecurePreferences
import org.thoughtcrime.securesms.dependencies.ManagerScope
import org.thoughtcrime.securesms.pro.ProStatusManager
import org.thoughtcrime.securesms.util.CurrentActivityObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayStoreSubscriptionManager @Inject constructor(
    private val application: Application,
    private val currentActivityObserver: CurrentActivityObserver,
    private val prefs: TextSecurePreferences,
    proStatusManager: ProStatusManager,
    @param:ManagerScope scope: CoroutineScope,
) : SubscriptionManager(proStatusManager, scope) {
    override val id = "google_play_store"
    override val name = "Google Play Store"
    override val description = ""
    override val iconRes = null

    override val supportsBilling: StateFlow<Boolean> = MutableStateFlow(false)

    override val availablePlans: List<ProSubscriptionDuration> = emptyList()

    override suspend fun purchasePlan(subscriptionDuration: ProSubscriptionDuration): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Billing disabled for interview build"))
    }

    override suspend fun hasValidSubscription(): Boolean {
        return prefs.forceCurrentUserAsPro()
    }

    override suspend fun getSubscriptionPrices(): List<SubscriptionManager.SubscriptionPricing> {
        return emptyList()
    }
}
