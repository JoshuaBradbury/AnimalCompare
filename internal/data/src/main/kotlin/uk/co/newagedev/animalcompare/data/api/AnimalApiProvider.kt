package uk.co.newagedev.animalcompare.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import uk.co.newagedev.animalcompare.domain.model.AnimalType

@Module
@InstallIn(SingletonComponent::class)
class AnimalApiProvider {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun getDogApi(): DogApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(AnimalType.Dog.endpoint)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(DogApi::class.java)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun getCatApi(): CatApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(AnimalType.Cat.endpoint)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(CatApi::class.java)
    }

}