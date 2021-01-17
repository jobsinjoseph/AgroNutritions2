package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class GetRefIdDataModel(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val refId_list: ArrayList<ReferralDetails>
)
