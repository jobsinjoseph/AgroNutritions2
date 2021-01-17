package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class RewardsDetails(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("first_level_min") val first_level_min: String,
    @SerializedName("second_level_min") val second_level_min: String,
    @SerializedName("gift") val gift: String
)