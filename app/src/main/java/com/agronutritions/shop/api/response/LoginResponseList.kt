package com.agronutritions.shop.api.response

import com.google.gson.annotations.SerializedName

data class LoginResponseList(
    @SerializedName("login_det") val login_det: ArrayList<LoginResponseDetails>
)