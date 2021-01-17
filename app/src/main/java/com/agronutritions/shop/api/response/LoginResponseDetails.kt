package com.agronutritions.shop.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginResponseDetails(
    @SerializedName("id") val cstmr_id: String = "",
    @SerializedName("name") val cstmr_name: String = "",
    @SerializedName("phone_number") val cstmr_mob: String = "",
    @SerializedName("address") val cstmr_address: String = "",
    @SerializedName("ref_id") val cstmr_ref_id: String = "",
    @SerializedName("ref_by") val cstmr_ref_by: String = "",
    //@SerializedName("cstmr_gmail") val cstmr_gmail: String,
    @SerializedName("email") val cstmr_gmail: String = "",
    @SerializedName("login_id") val cstmr_loginid: String = ""
): Parcelable