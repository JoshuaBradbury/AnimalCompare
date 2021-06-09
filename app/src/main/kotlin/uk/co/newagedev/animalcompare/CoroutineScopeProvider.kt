package uk.co.newagedev.animalcompare

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CoroutineScopeProvider {

    @Provides
    fun providesApplicationScope(application: AnimalCompareApplication) =
        application.applicationScope
}