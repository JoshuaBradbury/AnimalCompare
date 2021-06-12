package uk.co.newagedev.animalcompare.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import uk.co.newagedev.animalcompare.domain.model.AnimalType

@Module
@InstallIn(SingletonComponent::class)
class AnimalProvider {

    @Provides
    fun getDogApi(): DogApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(AnimalType.Dog.endpoint)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        return retrofit.create(DogApi::class.java)
    }

}