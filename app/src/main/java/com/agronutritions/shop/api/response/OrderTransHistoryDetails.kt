package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class OrderTransHistoryDetails(
    @SerializedName("ord_trans_id") val ord_trans_id: String,
    @SerializedName("ord_trans_masterid") val ord_trans_masterid: String,
    @SerializedName("ord_trans_pdtid") val ord_trans_pdtid: String,
    @SerializedName("ord_trans_unit_price") val ord_trans_unit_price: String,
    @SerializedName("ord_trans_qty") val ord_trans_qty: String
)