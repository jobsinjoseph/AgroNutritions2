package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class GenologyDetails(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("ref_id") val ref_id: String
)