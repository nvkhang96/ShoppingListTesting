package com.androiddevs.shoppinglisttestingyt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import com.androiddevs.shoppinglisttestingyt.other.Constants
import com.androiddevs.shoppinglisttestingyt.other.Resource
import com.androiddevs.shoppinglisttestingyt.repositories.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: ShoppingRepository
) : ViewModel() {

    val scope = CoroutineScope(Dispatchers.IO)

    val shoppingItems = repository.observeAllShoppingItems()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val totalPrice = repository.observeTotalPrice()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)


    private val _images = MutableStateFlow(
        Resource.success(
            ImageResponse(listOf(), 0, 0)
        )
    )
    val images: StateFlow<Resource<ImageResponse>> = _images

    private val _currentImageUrl = MutableStateFlow("")
    val currentImageUrl: StateFlow<String> = _currentImageUrl

    private val _insertShoppingItemStatusChannel = Channel<Resource<ShoppingItem>>()
    val insertShoppingItemStatus = _insertShoppingItemStatusChannel.receiveAsFlow()

    fun setCurImageUrl(url: String) {
        _currentImageUrl.update { url }
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    fun insertShoppingItem(name: String, amountString: String, priceString: String) {
        viewModelScope.launch {
            if (name.isEmpty() || amountString.isEmpty() || priceString.isEmpty()) {
                _insertShoppingItemStatusChannel.send(
                    Resource.error("The fields must not be empty", null)
                )
                return@launch
            }
            if (name.length > Constants.MAX_NAME_LENGTH) {
                _insertShoppingItemStatusChannel.send(
                    Resource.error(
                        "The name of the item must not excess ${Constants.MAX_NAME_LENGTH} characters",
                        null
                    )
                )
                return@launch
            }
            if (priceString.length > Constants.MAX_PRICE_LENGTH) {
                _insertShoppingItemStatusChannel.send(
                    Resource.error(
                        "The price of the item must not excess ${Constants.MAX_PRICE_LENGTH} characters",
                        null
                    )
                )
                return@launch
            }
            val amount = try {
                amountString.toInt()
            } catch (e: Exception) {
                _insertShoppingItemStatusChannel.send(
                    Resource.error("Please enter a valid amount", null)
                )
                return@launch
            }
            val shoppingItem =
                ShoppingItem(name, amount, priceString.toFloat(), _currentImageUrl.value)
            insertShoppingItemIntoDb(shoppingItem)
            setCurImageUrl("")
            _insertShoppingItemStatusChannel.send(Resource.success(shoppingItem))
        }
    }

    fun searchForImage(imageQuery: String) {
        if (imageQuery.isEmpty()) {
            return
        }
        _images.value = Resource.loading(null)
        viewModelScope.launch {
            val response = repository.searchForImage(imageQuery)
            _images.value = response
        }
    }
}