package com.iterika.marvel.catalog

import android.app.Dialog
import android.content.Context
import com.iterika.marvel.GlideApp
import com.iterika.marvel.R
import kotlinx.android.synthetic.main.dialog_add_cart.*

class AddCartDialog(context: Context, product: Product?, val listener: ((Int) -> Unit)? = null): Dialog(context) {

    var currentCount = 1

    init {
        setContentView(R.layout.dialog_add_cart)

        GlideApp.with(context).load(product?.picts?.firstOrNull()).fitCenter().into(image)
        name.text = product?.name?.trim()
        discount.text = "Скидка: -${product?.discount ?: 0} РУБ"

        ok.setOnClickListener {
            dismiss()
            listener?.invoke(currentCount)
        }

        plus.setOnClickListener {
            currentCount++
            count.text = "$currentCount шт."
        }

        minus.setOnClickListener {
            if (currentCount > 1) currentCount--
            count.text = "$currentCount шт."
        }

        cancel.setOnClickListener { dismiss() }
    }
}