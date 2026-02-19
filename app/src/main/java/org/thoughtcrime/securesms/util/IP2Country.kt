package org.thoughtcrime.securesms.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IP2Country internal constructor(
    private val context: Context
) {
    val countryNamesCache = mutableMapOf<String, String>()

    companion object {
        private val _countriesReady = MutableStateFlow(true)
        val countriesReady: StateFlow<Boolean> = _countriesReady

        lateinit var shared: IP2Country

        val isInitialized: Boolean get() = Companion::shared.isInitialized

        fun configureIfNeeded(context: Context) {
            if (isInitialized) { return; }
            shared = IP2Country(context.applicationContext)
        }
    }
}
