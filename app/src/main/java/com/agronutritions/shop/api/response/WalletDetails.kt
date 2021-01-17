package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class WalletDetails(
    @SerializedName("customer_id") val customer_id: String,
    @SerializedName("order_id") val order_id: String,
    @SerializedName("wallet_amount") val walletamt: String,
    @SerializedName("percentage") val percentage: String
)