package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

interface ShopListRepository {

    suspend fun getShopItemById(shopItemId: Int): ShopItem
    fun getShopList(): LiveData<List<ShopItem>>
    suspend fun deleteShopItem(shopItem: ShopItem)
    suspend fun changeShopItem(shopItem: ShopItem)
    suspend fun addShopItem(shopItem: ShopItem)

}