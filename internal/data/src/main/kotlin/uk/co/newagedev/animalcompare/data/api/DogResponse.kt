package uk.co.newagedev.animalcompare.data.api

import kotlinx.serialization.Serializable

@Serializable
data class DogResponse(
    val fileSizeBytes: Long,
    val url: String,
)