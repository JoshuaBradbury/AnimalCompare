package uk.co.newagedev.animalcompare.data.api

import kotlinx.serialization.Serializable

@Serializable
data class CatResponse(
    val file: String,
)