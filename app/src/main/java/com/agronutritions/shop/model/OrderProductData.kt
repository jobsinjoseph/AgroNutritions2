package com.agronutritions.shop.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["pdt_id"], unique = true)])
data class OrderProductData(
    @PrimaryKey var pdt_id: String,
    var pdt_name: String,
    var pdt_cat_id: String,
    var pdt_price: String,
    var pdt_qty: String,
    var pdt_total_price: String,
    var pdt_img: String
)