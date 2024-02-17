package com.example.shoppinglist.presentation

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("errorInputName")
fun bindErrorInputName(tilName: TextInputLayout, error: Boolean) {
    tilName.error = if (error) {
        "errorName"
    } else {
        null
    }
}

@BindingAdapter("errorInputCount")
fun bindErrorInputCount(tilCount: TextInputLayout, error: Boolean) {
    tilCount.error = if (error) {
        "errorName"
    } else {
        null
    }
}

@BindingAdapter("countText")
fun bindCountText(textInputEditText: TextInputEditText, amount: Int) {
    textInputEditText.setText(amount.toString())
}
