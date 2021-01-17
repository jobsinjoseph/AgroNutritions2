package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class PagerDetails(
    @SerializedName("id") val banner_id: String,
    @SerializedName("banner_image") val banner_img: String
   /* @SerializedName("banner_cat_id") val banner_cat_id: String*/
)