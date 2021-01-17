package com.agronutritions.shop.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agronutritions.shop.model.OrderProductData

@Dao
interface OrderDao {

    // Get all Products
    @Query("SELECT * from OrderProductData")
    suspend fun getCartProducts(): List<OrderProductData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderProduct(order: OrderProductData)

    @Query("DELETE from OrderProductData where pdt_id= :id")
    suspend fun removeProduct(id: String)

    @Query("DELETE from OrderProductData")
    suspend fun emptyCart()
}