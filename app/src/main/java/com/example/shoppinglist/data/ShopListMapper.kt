package com.example.shoppinglist.data

import com.example.shoppinglist.domain.ShopItem

class ShopListMapper {

    fun mapEntityToDbModel(shopItem: ShopItem): ShopItemDbModel {
        return ShopItemDbModel(
            id = shopItem.id,
            name = shopItem.name,
            amount = shopItem.amount,
            enabled = shopItem.enabled
        )
    }

    fun mapDbModelToEntity(shopItemDbModel: ShopItemDbModel): ShopItem {
        return ShopItem(
            id = shopItemDbModel.id,
            name = shopItemDbModel.name,
            amount = shopItemDbModel.amount,
            enabled = shopItemDbModel.enabled
        )
    }

    fun mapListDbModelToListEntities(dbModelList: List<ShopItemDbModel>): List<ShopItem> {
        return dbModelList.map { mapDbModelToEntity(it) }
    }
}