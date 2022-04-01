package com.gtbluesky.ultrastarter

object AppStarterManager {
    private val starterCacheSet = mutableSetOf<AppStarter>()

    internal fun putCache(appStarter: AppStarter) {
        starterCacheSet.add(appStarter)
    }

    @JvmStatic
    fun allowPrivacy() {
        starterCacheSet.forEach {
            it.startPrivacyInitializers()
        }
        starterCacheSet.clear()
    }
}