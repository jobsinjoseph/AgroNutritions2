package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class ProductDetails(
    @SerializedName("id") val pdt_id: String,
    @SerializedName("product_name") val pdt_name: String,
    @SerializedName("category_id") val pdt_cat_id: String,
    @SerializedName("product_image") val pdt_img: String,
    @SerializedName("product_price") val pdt_price: String,
    @SerializedName("product_offer_price") val pdt_offer_price: String,
    @SerializedName("product_code") val pdt_code: String,
    @SerializedName("product_description") val pdt_description: String,
    @SerializedName("deleted_status") val pdt_delete_status: String


) {
    override fun toString(): String {
        return pdt_name
    }
}