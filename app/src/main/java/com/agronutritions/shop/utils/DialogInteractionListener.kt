package com.agronutritions.shop.utils

interface DialogInteractionListener {

    fun onPositiveResponse(id:Int)

    fun onNegativeResponse(id:Int)
}