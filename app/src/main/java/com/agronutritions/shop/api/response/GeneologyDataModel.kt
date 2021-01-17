package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class GeneologyDataModel(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val geno_list: ArrayList<GenologyDetails>
)
