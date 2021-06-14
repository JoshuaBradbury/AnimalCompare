package uk.co.newagedev.animalcompare

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import coil.util.CoilUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class AnimalCompareApplication @Inject constructor() : Application(), ImageLoaderFactory {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        setupStrictMode()
        super.onCreate()
    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader
            .Builder(this)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(this))
                    .build()
            }
            .build()
    }
}