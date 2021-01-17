package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class CategoryList(
    @SerializedName("cat_list") val cat_list: ArrayList<CategoryDetails>
)