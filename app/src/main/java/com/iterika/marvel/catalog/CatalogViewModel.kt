package com.iterika.marvel.catalog

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.iterika.marvel.coupons.CouponResult
import com.iterika.marvel.coupons.CouponsRepo

class CatalogViewModel(val repo: CatalogRepo, val couponsRepo: CouponsRepo) : ViewModel() {

    var currentCatalogOffset = 0

    val catsData = MutableLiveData<List<Category>>()
    val retailData = MutableLiveData<List<Retail>>()
    val catalogData = MutableLiveData<List<Product>>()
    val searchData = MutableLiveData<List<Product>>()
    val fastCouponData = MutableLiveData<CouponResult>()

    fun getCats(force: Boolean) = repo.getCats(force) { catsData.value = it }

    fun getRetails() = repo.getRetails { retailData.value = it }

    fun getCatalog() = repo.getCatalog(currentCatalogOffset) {
        currentCatalogOffset += it?.size ?: 0
        catalogData.value = it
    }

    fun search(q: String) = repo.search(q) { searchData.value = it }

    fun fastCoupon(id: String, retail: String) = couponsRepo.getFastCoupon(id, retail) { fastCouponData.value = it }

}