package com.iterika.marvel.coupons

import com.iterika.marvel.catalog.Product

data class OrderDetails(
    val id: String? = null,
    val items: List<Product>? = null,
    val retailLogo: String? = null,
    val retailId: String? = null,
    val date: String? = null,
    val status: String? = null,
    val err: String? = null,
    val barcode: String? = null,
    val count: String? = null
)