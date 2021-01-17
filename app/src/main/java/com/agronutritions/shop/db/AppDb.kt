package com.agronutritions.shop.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.agronutritions.shop.db.dao.OrderDao
import com.agronutritions.shop.model.OrderProductData

@Database(
    entities = [(OrderProductData::class)],
    version = 1
)
abstract class AppDb: RoomDatabase() {
    abstract fun getCartDao(): OrderDao
}