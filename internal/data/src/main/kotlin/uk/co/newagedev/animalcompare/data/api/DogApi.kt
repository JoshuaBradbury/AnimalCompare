package uk.co.newagedev.animalcompare.data.api

import retrofit2.http.GET

interface DogApi {

    @GET("woof")
    suspend fun getRandomDog(): String
}