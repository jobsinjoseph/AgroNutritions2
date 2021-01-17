package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class SignupResponseDetails(
    @SerializedName("cstmr_id") val cstmr_id: String,
    @SerializedName("cstmr_name") val cstmr_name: String,
    @SerializedName("cstmr_mob") val cstmr_mob: String,
    @SerializedName("cstmr_gmail") val cstmr_gmail: String,
    @SerializedName("cstmr_loginid") val cstmr_loginid: String
)