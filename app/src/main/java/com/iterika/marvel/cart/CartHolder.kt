package com.iterika.marvel.cart

import com.iterika.marvel.catalog.Product

class CartHolder {
    var products = mutableListOf<Product>()

    fun addOrMerge(product: Product) {
        val found = products.find { it.id == product.id }
        if (found != null) {
            found.count += product.count
        } else {
            products.add(product.copy())
        }
    }
}