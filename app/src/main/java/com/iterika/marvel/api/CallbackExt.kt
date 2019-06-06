package com.iterika.marvel.api

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.enqueue(
    success: (response: Response<T>) -> Unit,
    failure: (t: Throwable) -> Unit,
    error: (response: Error?) -> Unit
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) {
            if (response.isSuccessful) {
                success(response)
            } else {
                error(parseError(response))
            }
        }

        override fun onFailure(call: Call<T>?, t: Throwable) = failure(t)
    })
}

fun parseError(response: Response<*>): Error? {
    return try {
        val json = response.errorBody()?.string()
        Gson().fromJson(json, Error::class.java)
    } catch (e: Exception) {
        null
    }
}