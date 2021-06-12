package uk.co.newagedev.animalcompare.data

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ImageLoaderProvider {

    @Singleton
    @Provides
    fun providesImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader
            .Builder(context)
            .crossfade(true)
            .build()
    }
}