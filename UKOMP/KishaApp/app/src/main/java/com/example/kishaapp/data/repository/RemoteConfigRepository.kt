package com.example.kishaapp.data.repository

import com.example.kishaapp.data.remote.FirebaseProvider
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

class RemoteConfigRepository(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseProvider.remoteConfig
) {

    suspend fun fetchAndActivate(): Result<Unit> {
        return runCatching {
            val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
            remoteConfig.setConfigSettingsAsync(settings).await()
            remoteConfig.setDefaultsAsync(
                mapOf(
                    KEY_ALLOW_STORAGE_UPLOAD to true,
                    KEY_SELLER_FEATURE_ENABLED to true,
                    KEY_HOME_BANNER_TEXT to "Selamat datang di Kisha Marketplace"
                )
            ).await()
            remoteConfig.fetchAndActivate().await()
            Unit
        }
    }

    fun isStorageUploadAllowed(): Boolean {
        return remoteConfig.getBoolean(KEY_ALLOW_STORAGE_UPLOAD)
    }

    fun isSellerFeatureEnabled(): Boolean {
        return remoteConfig.getBoolean(KEY_SELLER_FEATURE_ENABLED)
    }

    fun homeBannerText(): String {
        return remoteConfig.getString(KEY_HOME_BANNER_TEXT)
    }

    companion object {
        const val KEY_ALLOW_STORAGE_UPLOAD = "allow_storage_upload"
        const val KEY_SELLER_FEATURE_ENABLED = "seller_feature_enabled"
        const val KEY_HOME_BANNER_TEXT = "home_banner_text"
    }
}
