package uk.co.newagedev.animalcompare.data.api

import retrofit2.http.GET

interface FoxApi {

    @GET("floof/")
    suspend fun getRandomFox(): FoxResponse
}