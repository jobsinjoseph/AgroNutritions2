package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class StateDetails(
    @SerializedName("id") val id: String,
    @SerializedName("state") val state: String

)