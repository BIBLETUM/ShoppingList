package com.example.shoppinglist.domain

class ChangeShopItemUseCase(private val shopListRepository: ShopListRepository) {

    suspend fun changeShopItem(shopItem: ShopItem) {
        shopListRepository.changeShopItem(shopItem)
    }
}