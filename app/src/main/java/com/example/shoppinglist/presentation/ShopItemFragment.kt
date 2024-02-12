package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment : Fragment() {

    private lateinit var shopItemViewModel: ShopItemViewModel

    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etCount: TextInputEditText
    private lateinit var buttonSave: Button

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopItemViewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews(view)

        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
        validateTexts()
        checkShouldCloseScreen()
    }

    private fun launchEditMode() {
        shopItemViewModel.getShopItem(shopItemId)
        shopItemViewModel.item.observe(viewLifecycleOwner) {
            etName.setText(it.name)
            etCount.setText(it.amount.toString())
        }
        buttonSave.setOnClickListener {
            val name = etName.text?.toString()
            val count = etCount.text?.toString()
            shopItemViewModel.changeShopItem(name, count)
        }
    }

    private fun launchAddMode() {
        buttonSave.setOnClickListener {
            val name = etName.text?.toString()
            val count = etCount.text?.toString()
            shopItemViewModel.addShopItem(name, count)
        }
    }

    private fun checkShouldCloseScreen() {
        shopItemViewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            activity?.finish()
        }
    }

    private fun validateTexts() {
        shopItemViewModel.errorInputName.observe(viewLifecycleOwner) {
            tilName.error = if (it) {
                "errorName"
            } else {
                null
            }
        }
        shopItemViewModel.errorInputCount.observe(viewLifecycleOwner) {
            tilCount.error = if (it) {
                "errorCount"
            } else {
                null
            }
        }
        etName.doOnTextChanged { _, _, _, _ ->
            shopItemViewModel.resetErrorInputName()
            tilName.error = null
        }
        etCount.doOnTextChanged { _, _, _, _ ->
            shopItemViewModel.resetErrorInputCount()
            tilCount.error = null
        }
    }

    private fun initViews(view: View) {
        tilName = view.findViewById(R.id.til_name)
        tilCount = view.findViewById(R.id.til_count)
        etName = view.findViewById(R.id.et_name)
        etCount = view.findViewById(R.id.et_count)
        buttonSave = view.findViewById(R.id.buttonSave)
    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(EXTRA_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(EXTRA_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode: $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = args.getInt(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    companion object {

        private const val EXTRA_MODE = "mode"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val EXTRA_SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceChangeItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_MODE, MODE_EDIT)
                    putInt(EXTRA_SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }

}