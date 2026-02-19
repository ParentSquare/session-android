package org.thoughtcrime.securesms.reviews

import android.app.Application
import org.thoughtcrime.securesms.util.CurrentActivityObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayStoreReviewManager @Inject constructor(
    private val currentActivityObserver: CurrentActivityObserver,
    private val application: Application,
) : StoreReviewManager {

    override val storeName: String = "Google Play Store"

    override val supportsReviewFlow: Boolean = false

    override suspend fun requestReviewFlow() {
        // No-op: Review flow disabled for interview build
    }
}
