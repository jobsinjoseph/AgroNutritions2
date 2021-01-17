package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class PinList(
    @SerializedName("pin_list") val pin_list: ArrayList<PinDetails>
)