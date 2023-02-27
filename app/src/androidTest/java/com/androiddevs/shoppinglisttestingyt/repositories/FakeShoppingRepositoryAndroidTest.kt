package com.androiddevs.shoppinglisttestingyt.repositories

import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import com.androiddevs.shoppinglisttestingyt.other.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeShoppingRepositoryAndroidTest : ShoppingRepository {

    private val shoppingItems = mutableListOf<ShoppingItem>()

    private val observableShoppingItems = MutableStateFlow<List<ShoppingItem>>(shoppingItems)
    private val observableTotalPrice = MutableStateFlow<Float>(0f)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

//    private suspend fun refreshFlow() {
//        observableShoppingItems.update { emptyList() }
//        observableShoppingItems.update { shoppingItems }
//        observableTotalPrice.emit(getTotalPrice())
//    }

    private fun getTotalPrice(): Float {
        return shoppingItems.sumOf { it.price.toDouble() }.toFloat()
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
//        refreshFlow()
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.remove(shoppingItem)
//        refreshFlow()
    }

    override fun observeAllShoppingItems(): Flow<List<ShoppingItem>> {
        return flow {
            emit(shoppingItems)
        }
    }

    override fun observeTotalPrice(): Flow<Float> {
        return flow {
            emit(getTotalPrice())
        }
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Error", null)
        } else {
            Resource.success(ImageResponse(listOf(), 0, 0))
        }
    }
}