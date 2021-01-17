package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class ReportMenuDetails(
    @SerializedName("menu_id") val menu_id: String,
    @SerializedName("menu_name") val menu_name: String,
    @SerializedName("pic") val pic: String

)