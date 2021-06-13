package uk.co.newagedev.animalcompare.data.api

import retrofit2.http.GET

interface CatApi {

    @GET("meow")
    suspend fun getRandomCat(): CatResponse
}