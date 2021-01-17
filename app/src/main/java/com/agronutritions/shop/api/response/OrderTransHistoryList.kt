package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class OrderTransHistoryList(
    @SerializedName("ordr_single_det") val ordr_single_det: ArrayList<OrderTransHistoryDetails>
)