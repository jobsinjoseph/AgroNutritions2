package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class DeliveryAddressdetailsData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("phone_number") val phone_number: String,


    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String,
    @SerializedName("city") val city: String,
    @SerializedName("pincode") val pincode: String,

///
    @SerializedName("house_name") val house_name: String,
@SerializedName("street") val street: String,
@SerializedName("town") val town: String




)