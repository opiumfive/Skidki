package com.iterika.marvel.coupons

import com.iterika.marvel.Prefs
import com.iterika.marvel.api.IApi
import com.iterika.marvel.api.enqueue
import com.iterika.marvel.utils.DateFormatter

class CouponsRepo(private val api: IApi, private val prefs: Prefs, private val dateFormatter: DateFormatter) {

    val currentCoupons = mutableListOf<Coupon>()

    fun clearCache() = currentCoupons.clear()

    fun getHistory(force: Boolean, result: (List<Coupon>?) -> Unit) {
        if (force) {
            currentCoupons.clear()
        }

        if (currentCoupons.isNotEmpty()) {
            result.invoke(currentCoupons)
            return
        }

        api.history(prefs.token).enqueue(
            {
                val cats = it.body()?.sortedByDescending { it.id }
                result.invoke(cats)
                cats?.forEach { currentCoupons.add(it) }
            }, {
                result.invoke(emptyList())
            }, {
                result.invoke(emptyList())
            }
        )
    }

    fun getFastCoupon(id: String, retail: String, result: (CouponResult?) -> Unit) {
        api.quickCoupon(id, retail, if (prefs.token.isNotEmpty()) prefs.token else null).enqueue(
            {
                result.invoke(it.body())
            }, {
                result.invoke(null)
            }, {
                result.invoke(CouponResult(err = it?.err))
            }
        )
    }

    fun checkout(ids: String, retail: String, result: (CouponResult?) -> Unit) {
        api.checkout(ids, retail, prefs.token).enqueue(
            {
                result.invoke(it.body())
            }, {
                result.invoke(null)
            }, {
                result.invoke(CouponResult(err = it?.err))
            }
        )
    }

    fun orderDetails(id: String, result: (OrderDetails?) -> Unit) {
        api.orderDetails(id, prefs.token).enqueue(
            {
                result.invoke(it.body())
            }, {
                result.invoke(null)
            }, {
                result.invoke(OrderDetails(err = it?.err))
            }
        )
    }
}