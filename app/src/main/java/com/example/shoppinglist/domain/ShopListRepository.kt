package com.example.shoppinglist.domain

interface ShopListRepository {

    fun getShopItemById(shopItemId: Int): ShopItem
    fun getShopList(): List<ShopItem>
    fun deleteShopItem(shopItem: ShopItem)
    fun changeShopItem(shopItem: ShopItem)
    fun addShopItem(shopItem: ShopItem)

}