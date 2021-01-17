package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class BankdetailsData(
    @SerializedName("bank_holder") val bank_holder: String,
    @SerializedName("bank_account_number") val bank_account_number: String,
    @SerializedName("bank_name") val bank_name: String,
    @SerializedName("bank_branch") val bank_branch: String,
    @SerializedName("bank_ifsc") val bank_ifsc: String,
    @SerializedName("customer_id") val customer_id: String
)