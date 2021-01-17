package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class OrderHistoryList(
    @SerializedName("ordr_history_det") val ordr_history_det: ArrayList<OrderHistoryDetails>
)