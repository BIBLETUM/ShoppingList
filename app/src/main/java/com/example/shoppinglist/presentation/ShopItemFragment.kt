package com.example.shoppinglist.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.databinding.FragmentShopItemBinding
import com.example.shoppinglist.domain.ShopItem
import javax.inject.Inject

class ShopItemFragment : Fragment() {

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == null")

    private lateinit var shopItemViewModel: ShopItemViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (requireActivity().application as ShoppingListApp).component
    }

    private lateinit var onSaveButtonClickListener: OnSaveButtonClickListener

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
        if (context is OnSaveButtonClickListener) {
            onSaveButtonClickListener = context
        } else {
            throw RuntimeException("Activity must implement OnSaveButtonClickListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopItemViewModel = ViewModelProvider(this, viewModelFactory)[ShopItemViewModel::class.java]

        binding.viewModel = shopItemViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        validateTexts()
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
        checkShouldCloseScreen()
    }

    private fun launchEditMode() {
        shopItemViewModel.getShopItem(shopItemId)
        binding.buttonSave.setOnClickListener {
            val name = binding.etName.text?.toString()
            val count = binding.etCount.text?.toString()
            shopItemViewModel.changeShopItem(name, count)
        }
    }

    private fun launchAddMode() {
        binding.buttonSave.setOnClickListener {
            val name = binding.etName.text?.toString()
            val count = binding.etCount.text?.toString()
            shopItemViewModel.addShopItem(name, count)
        }
    }

    private fun checkShouldCloseScreen() {
        shopItemViewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
            onSaveButtonClickListener.onSaveButtonClick()
        }
    }

    private fun validateTexts() {
        binding.etName.doOnTextChanged { _, _, _, _ ->
            shopItemViewModel.resetErrorInputName()
            binding.tilName.error = null
        }
        binding.etCount.doOnTextChanged { _, _, _, _ ->
            shopItemViewModel.resetErrorInputCount()
            binding.tilCount.error = null
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    interface OnSaveButtonClickListener {
        fun onSaveButtonClick()
    }
}