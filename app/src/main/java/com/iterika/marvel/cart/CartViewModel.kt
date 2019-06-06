package com.iterika.marvel.cart

import com.iterika.marvel.coupons.CouponsRepo
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.iterika.marvel.coupons.CouponResult

class CartViewModel(private val repo: CouponsRepo): ViewModel() {

    val checkoutData = MutableLiveData<CouponResult>()

    fun checkout(ids: String, retail: String) = repo.checkout(ids, retail) { checkoutData.value = it }
}