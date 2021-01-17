package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class CategoryDetails(
    @SerializedName("id") val cat_id: String,
    @SerializedName("category_name") val cat_name: String,
    @SerializedName("category_images") val cat_img: String
)