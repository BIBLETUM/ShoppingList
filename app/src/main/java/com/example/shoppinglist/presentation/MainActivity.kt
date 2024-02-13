package com.example.shoppinglist.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ShopItemFragment.OnSaveButtonClickListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var rvShopList: RecyclerView
    private lateinit var buttonAddShopItem: FloatingActionButton

    private var shopItemContainer: FragmentContainerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shopItemContainer = findViewById(R.id.shop_item_container)

        initViews()
        setUpListeners()

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.shopList.observe(this)  {
            shopListAdapter.submitList(it)
        }
    }

    private fun fragmentWork(shopItemId: Int?) {
        supportFragmentManager.popBackStack()
        val fragment = if (shopItemId == null) {
            ShopItemFragment.newInstanceAddItem()
        } else {
            ShopItemFragment.newInstanceChangeItem(shopItemId)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun initViews() {
        rvShopList = findViewById(R.id.rv_shop_list)
        buttonAddShopItem = findViewById(R.id.button_add_shop_item)
        rvShopList.recycledViewPool.setMaxRecycledViews(
            ShopListAdapter.VIEW_TYPE_ENABLED,
            ShopListAdapter.POOL_MAX_SIZE
        )
        rvShopList.recycledViewPool.setMaxRecycledViews(
            ShopListAdapter.VIEW_TYPE_DISABLED,
            ShopListAdapter.POOL_MAX_SIZE
        )
        shopListAdapter = ShopListAdapter()
        rvShopList.adapter = shopListAdapter
    }

    override fun onSaveButtonClick() {
        supportFragmentManager.popBackStack()
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
    }

    private fun setUpListeners() {
        shopListAdapter.onItemLongClickListener = {
            mainViewModel.changeEnableState(it)
        }
        shopListAdapter.onItemClickListener = {
            if (shopItemContainer == null) {
                val intent = ShopItemActivity.newIntentChangeItem(this, it.id)
                startActivity(intent)
            } else {
                fragmentWork(it.id)
            }
        }
        buttonAddShopItem.setOnClickListener {
            if (shopItemContainer == null) {
                val intent = ShopItemActivity.newIntentAddItem(this)
                startActivity(intent)
            } else {
                fragmentWork(null)
            }
        }

        val callBack = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]
                mainViewModel.deleteShopItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }
}