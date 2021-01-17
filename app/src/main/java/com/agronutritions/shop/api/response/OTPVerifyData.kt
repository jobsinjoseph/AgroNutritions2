package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class OTPVerifyData(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Int
)