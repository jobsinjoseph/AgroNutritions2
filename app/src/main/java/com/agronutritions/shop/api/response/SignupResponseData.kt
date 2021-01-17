package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class SignupResponseData(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    //@SerializedName("data") val data: ArrayList<SignupResponseDetails>
    @SerializedName("data") val data: Int
)