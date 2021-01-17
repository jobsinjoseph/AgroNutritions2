package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class ReportMenusDataModel(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val menu_list: ArrayList<ReportMenuDetails>
)
