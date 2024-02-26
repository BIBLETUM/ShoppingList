package com.example.shoppinglist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.AddShopItemUseCase
import com.example.shoppinglist.domain.ChangeShopItemUseCase
import com.example.shoppinglist.domain.GetShopItemByIdUseCase
import com.example.shoppinglist.domain.ShopItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ShopItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ShopListRepositoryImpl(application)

    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val changeShopItemUseCase = ChangeShopItemUseCase(repository)
    private val getShopItemUseCase = GetShopItemByIdUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _item = MutableLiveData<ShopItem>()
    val item: LiveData<ShopItem>
        get() = _item

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen


    fun getShopItem(shopItemId: Int) {
        viewModelScope.launch {
            _item.value = getShopItemUseCase.getShopItemById(shopItemId)
        }
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        viewModelScope.launch {
            val name = parseName(inputName)
            val count = parseCount(inputCount)
            val isValid = validateInput(name, count)
            if (isValid) {
                val shopItem = ShopItem(name, count, true)
                addShopItemUseCase.addShopItem(shopItem)
                _shouldCloseScreen.value = Unit
            }
        }
    }

    fun changeShopItem(inputName: String?, inputCount: String?) {
        viewModelScope.launch {
            val name = parseName(inputName)
            val count = parseCount(inputCount)
            val isValid = validateInput(name, count)
            if (isValid) {
                _item.value?.let {
                    val shopItem = it.copy(name = name, amount = count)
                    changeShopItemUseCase.changeShopItem(shopItem)
                    _shouldCloseScreen.value = Unit
                }
            }
        }
    }

    private fun parseName(name: String?): String {
        return name?.trim() ?: ""
    }

    private fun parseCount(count: String?): Int {
        return try {
            count?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }
}