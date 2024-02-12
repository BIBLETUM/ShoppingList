package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

interface ShopListRepository {

    fun getShopItemById(shopItemId: Int): ShopItem
    fun getShopList(): LiveData<List<ShopItem>>
    fun deleteShopItem(shopItem: ShopItem)
    fun changeShopItem(shopItem: ShopItem)
    fun addShopItem(shopItem: ShopItem)

}