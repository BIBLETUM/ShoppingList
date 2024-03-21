package com.example.shoppinglist.domain

import javax.inject.Inject

class ChangeShopItemUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {

    suspend fun changeShopItem(shopItem: ShopItem) {
        shopListRepository.changeShopItem(shopItem)
    }
}