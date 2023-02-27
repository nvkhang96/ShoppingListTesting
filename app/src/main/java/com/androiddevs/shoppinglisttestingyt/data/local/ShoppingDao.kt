package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    fun deleteShoppingItem(shoppingItem: ShoppingItem)

    @Query("SELECT * FROM shopping_items")
    fun observeAllShoppingItems(): Flow<List<ShoppingItem>>

    @Query("SELECT IFNULL(SUM(price * amount), 0) FROM shopping_items")
    fun observeTotalPrice(): Flow<Float>
}