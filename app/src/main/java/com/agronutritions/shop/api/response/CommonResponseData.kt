package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class CommonResponseData(
    @SerializedName("result") val result: String
)