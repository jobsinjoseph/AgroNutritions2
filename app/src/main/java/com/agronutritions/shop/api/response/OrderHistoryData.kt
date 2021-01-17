package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class OrderHistoryData(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val ordr_history_det: ArrayList<OrderHistoryDetails>
)