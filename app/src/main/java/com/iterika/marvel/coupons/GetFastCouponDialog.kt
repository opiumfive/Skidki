package com.iterika.marvel.coupons

import android.app.Dialog
import android.content.Context
import com.iterika.marvel.R
import kotlinx.android.synthetic.main.dialog_get_coupon.*

class GetFastCouponDialog(context: Context, val listener: (() -> Unit)? = null): Dialog(context) {

    init {
        setContentView(R.layout.dialog_get_now_coupon)

        ok.setOnClickListener {
            dismiss()
            listener?.invoke()
        }

        cancel.setOnClickListener { dismiss() }
    }
}