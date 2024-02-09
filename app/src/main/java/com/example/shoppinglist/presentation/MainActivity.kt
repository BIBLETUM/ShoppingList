package com.example.shoppinglist.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var rv_shop_list: RecyclerView
    private lateinit var button_add_shop_item: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setUpListeners()

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.shopList.observe(this, Observer {
            shopListAdapter.shopList = it
        })
    }

    private fun initViews() {
        rv_shop_list = findViewById(R.id.rv_shop_list)
        button_add_shop_item = findViewById(R.id.button_add_shop_item)
        rv_shop_list.recycledViewPool.setMaxRecycledViews(
            ShopListAdapter.VIEW_TYPE_ENABLED,
            ShopListAdapter.POOL_MAX_SIZE
        )
        rv_shop_list.recycledViewPool.setMaxRecycledViews(
            ShopListAdapter.VIEW_TYPE_DISABLED,
            ShopListAdapter.POOL_MAX_SIZE
        )
        shopListAdapter = ShopListAdapter()
        rv_shop_list.adapter = shopListAdapter
    }

    private fun setUpListeners() {
        shopListAdapter.onItemLongClickListener = {
            mainViewModel.changeEnableState(it)
        }
        shopListAdapter.onItemClickListener = {
            Log.d("Aoba", it.toString())
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
                val item = shopListAdapter.shopList[viewHolder.adapterPosition]
                mainViewModel.deleteShopItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(rv_shop_list)
    }
}