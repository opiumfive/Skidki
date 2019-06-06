package com.iterika.marvel.coupons

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class CouponsViewModel(val repo: CouponsRepo): ViewModel() {

    val couponsData = MutableLiveData<List<Coupon>>()
    val couponDetailData = MutableLiveData<OrderDetails>()

    fun getCoupons(force: Boolean) = repo.getHistory(force) { couponsData.value = it }

    fun getDetail(id: String) = repo.orderDetails(id) { couponDetailData.value = it }

}