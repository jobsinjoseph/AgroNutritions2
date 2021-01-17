package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class PinDetails(
    @SerializedName("pin_id") val pin_id: String,
    @SerializedName("pin_num") val pin_num: String
)