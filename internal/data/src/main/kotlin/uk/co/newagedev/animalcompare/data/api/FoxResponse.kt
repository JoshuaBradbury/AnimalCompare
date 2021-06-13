package uk.co.newagedev.animalcompare.data.api

import kotlinx.serialization.Serializable

@Serializable
data class FoxResponse(
    val image: String,
    val link: String,
)