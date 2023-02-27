package com.androiddevs.shoppinglisttestingyt.repositories

import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import com.androiddevs.shoppinglisttestingyt.other.Resource
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): Flow<List<ShoppingItem>>

    fun observeTotalPrice(): Flow<Float>

    suspend fun searchForImage(imageQuery: String): Resource<ImageResponse>
}