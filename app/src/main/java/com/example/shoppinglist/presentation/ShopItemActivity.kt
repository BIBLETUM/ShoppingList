package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopItemActivity : AppCompatActivity() {

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID
    private lateinit var fragment: ShopItemFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        parseIntent()

        fragment = when (screenMode) {
            MODE_ADD -> ShopItemFragment.newInstanceAddItem()
            MODE_EDIT -> ShopItemFragment.newInstanceChangeItem(shopItemId)
            else -> throw RuntimeException("Unknown screen mode: $screenMode")
        }
        supportFragmentManager.beginTransaction().add(R.id.shop_item_container, fragment).commit()
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = intent.getStringExtra(EXTRA_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode: $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    companion object {
        private const val EXTRA_MODE = "mode"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val EXTRA_SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_UNKNOWN = ""

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_MODE, MODE_ADD)
            return intent
        }

        fun newIntentChangeItem(context: Context, itemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, itemId)
            intent.putExtra(EXTRA_MODE, MODE_EDIT)
            return intent
        }
    }
}