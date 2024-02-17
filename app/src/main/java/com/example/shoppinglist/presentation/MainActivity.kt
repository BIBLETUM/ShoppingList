package com.example.shoppinglist.presentation

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ShopItemFragment.OnSaveButtonClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.rvShopList.recycledViewPool.setMaxRecycledViews(
            ShopListAdapter.VIEW_TYPE_ENABLED,
            ShopListAdapter.POOL_MAX_SIZE
        )
        binding.rvShopList.recycledViewPool.setMaxRecycledViews(
            ShopListAdapter.VIEW_TYPE_DISABLED,
            ShopListAdapter.POOL_MAX_SIZE
        )
        shopListAdapter = ShopListAdapter()
        binding.rvShopList.adapter = shopListAdapter
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
            if (binding.shopItemContainer == null) {
                val intent = ShopItemActivity.newIntentChangeItem(this, it.id)
                startActivity(intent)
            } else {
                fragmentWork(it.id)
            }
        }
        binding.buttonAddShopItem.setOnClickListener {
            if (binding.shopItemContainer == null) {
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
        itemTouchHelper.attachToRecyclerView(binding.rvShopList)
    }
}