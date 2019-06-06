package com.iterika.marvel.catalog

import android.text.TextUtils
import com.iterika.marvel.api.IApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogRepo(private val api: IApi) {

    val currentProducts = mutableListOf<Product>()
    val currentCats = mutableListOf<Category>()
    val currentRetails = mutableListOf<Retail>()
    val retailsFilter = mutableListOf<Retail>()
    val catsFilter = mutableListOf<Category>()
    var needToRefresh = false

    fun getCats(force: Boolean, result: (List<Category>?) -> Unit) {
        if (force) {
            currentCats.clear()
        }

        if (currentCats.isNotEmpty()) {
            result.invoke(currentCats)
            return
        }

        api.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                result.invoke(emptyList())
            }

            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val cats = response.body()
                    result.invoke(cats)
                    cats?.forEach { currentCats.add(it) }
                } else {
                    result.invoke(emptyList())
                }
            }
        })
    }

    fun getRetails(result: (List<Retail>?) -> Unit) {
        if (currentRetails.isNotEmpty()) {
            result.invoke(currentRetails)
            return
        }

        api.getRetails().enqueue(object : Callback<List<Retail>> {
            override fun onFailure(call: Call<List<Retail>>, t: Throwable) {
                result.invoke(emptyList())
            }

            override fun onResponse(call: Call<List<Retail>>, response: Response<List<Retail>>) {
                if (response.isSuccessful) {
                    val rets = response.body()
                    result.invoke(rets)
                    rets?.forEach { currentRetails.add(it) }
                } else {
                    result.invoke(emptyList())
                }
            }
        })
    }

    fun getCatalog(offset: Int = 0, result: (List<Product>?) -> Unit) {
        if (offset == 0 && currentProducts.isNotEmpty()) {
            result.invoke(currentProducts)
            return
        }

        api.getCatalog(
            offset = offset,
            category = if (catsFilter.isNullOrEmpty()) null else TextUtils.join(",", catsFilter.map { it.id }),
            retail = if (retailsFilter.isNullOrEmpty()) null else TextUtils.join(",", retailsFilter.map { it.id })
        ).enqueue(object : Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                result.invoke(emptyList())
            }

            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body()
                    result.invoke(products)

                    if (offset == 0) currentProducts.clear()
                    products?.forEach { currentProducts.add(it) }
                } else {
                    result.invoke(emptyList())
                }
            }
        })
    }

    fun search(query: String, result: (List<Product>?) -> Unit) {
        api.search(q = query).enqueue(object : Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                result.invoke(emptyList())
            }

            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    result.invoke(response.body())
                } else {
                    result.invoke(emptyList())
                }
            }
        })
    }
}