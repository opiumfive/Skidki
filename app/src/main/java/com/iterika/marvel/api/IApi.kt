package com.iterika.marvel.api

import com.iterika.marvel.auth.AuthResult
import com.iterika.marvel.auth.TotalSaved
import com.iterika.marvel.catalog.Category
import com.iterika.marvel.catalog.Product
import com.iterika.marvel.catalog.Retail
import com.iterika.marvel.coupons.Coupon
import com.iterika.marvel.coupons.CouponResult
import com.iterika.marvel.coupons.OrderDetails
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface IApi {

    @POST("./")
    fun getCategories(
        @Query("action") action: String = "***"
    ): Call<List<Category>>

    @POST("./")
    fun getRetails(
        @Query("action") action: String = "***"
    ): Call<List<Retail>>

    @POST("./")
    fun getCatalog(
        @Query("action") action: String = "***",
        @Query("offset") offset: Int? = null,
        @Query("count") count: Int = 10,
        @Query("category") category: String? = null,
        @Query("retail") retail: String? = null
    ): Call<List<Product>>

    @POST("./")
    fun search(
        @Query("action") action: String = "***",
        @Query("q") q: String? = null
    ): Call<List<Product>>

    @FormUrlEncoded
    @POST("./")
    fun register(
        @Field("user") user: String,
        @Field("password") password: String,
        @Query("action") action: String = "***"
    ): Call<AuthResult>

    @FormUrlEncoded
    @POST("./")
    fun login(
        @Field("user") user: String,
        @Field("password") password: String,
        @Query("action") action: String = "***"
    ): Call<AuthResult>

    @FormUrlEncoded
    @POST("./")
    fun quickCoupon(
        @Field("item") id: String,
        @Field("retail") retail: String,
        @Field("token") user: String? = null,
        @Query("action") action: String = "***"
    ): Call<CouponResult>

    @FormUrlEncoded
    @POST("./")
    fun checkout(
        @Field("Items") id: String,
        @Field("retail") retail: String,
        @Field("token") user: String,
        @Query("action") action: String = "***"
    ): Call<CouponResult>

    @POST("./")
    fun orderDetails(
        @Query("id") id: String,
        @Query("token") token: String,
        @Query("action") action: String = "***"
    ): Call<OrderDetails>

    @FormUrlEncoded
    @POST("./")
    fun resetPassword(
        @Field("email") email: String,
        @Query("action") action: String = "***"
    ): Call<AuthResult>

    @POST("./")
    fun history(
        @Query("token") token: String,
        @Query("action") action: String = "***"
    ): Call<List<Coupon>>

    @POST("./")
    fun totalSaved(
        @Query("token") token: String,
        @Query("action") action: String = "***"
    ): Call<TotalSaved>
}