package com.agronutritions.shop.db.entities

import androidx.room.PrimaryKey

//@Entity(indices = [Index(value = ["productId"], unique = true)])
data class Order(
    @PrimaryKey var productId: String,
    var productName: String,
    var productCatId: String,
    var productPrice: String,
    var productQty: String,
    var productSum: String

)