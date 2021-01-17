package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class ProductList(
    @SerializedName("product_det") val product_det: ArrayList<ProductDetails>
)