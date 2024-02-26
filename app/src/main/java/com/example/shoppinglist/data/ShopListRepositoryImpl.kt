package com.example.shoppinglist.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopItem.Companion.UNDEFINED_ID
import com.example.shoppinglist.domain.ShopListRepository
import java.lang.RuntimeException
import kotlin.random.Random

class ShopListRepositoryImpl (
    application: Application
): ShopListRepository {

    private val shopListDao = AppDatabase.getInstance(application).shopListDao()
    private val mapper = ShopListMapper()

    override suspend fun getShopItemById(shopItemId: Int): ShopItem {
        val shopItemFromDb = shopListDao.getShopItem(shopItemId)
        return mapper.mapDbModelToEntity(shopItemFromDb)
    }

    override fun getShopList(): LiveData<List<ShopItem>> {
        val shopListFromDb = shopListDao.getShopList()
        return shopListFromDb.map { mapper.mapListDbModelToListEntities(it) }
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override suspend fun changeShopItem(shopItem: ShopItem) {
        addShopItem(shopItem)
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }
}