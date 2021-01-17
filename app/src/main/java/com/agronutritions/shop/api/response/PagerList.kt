package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class PagerList(
    @SerializedName("bann_list") val bann_list: ArrayList<PagerDetails>
)