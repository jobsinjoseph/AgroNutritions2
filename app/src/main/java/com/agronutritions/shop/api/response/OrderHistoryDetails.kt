package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class OrderHistoryDetails(
    @SerializedName("customer_id") val customer_id: String,
    @SerializedName("order_id") val order_id: String,
    @SerializedName("order_status") val order_status: String,
    @SerializedName("order_date") val order_date: String,
    @SerializedName("delivered_date") val delivered_date: String,
    @SerializedName("product_name") val product_name: String,
    @SerializedName("amount") val amount: String
)