package com.agronutritions.shop.extensions

import android.app.Dialog
import android.content.Context

/**
 * showDialog - extension method to show Dialog
 */
fun showDialog(context: Context, builder: Dialog.() -> Unit){
    val dialogView = Dialog(context, android.R.style.Theme_Dialog)

    try {
        if (dialogView!=null && dialogView.isShowing){
            dialogView.dismiss()
        }
        dialogView.builder()
        dialogView.show()
    } catch (e: Exception) {
    }
}