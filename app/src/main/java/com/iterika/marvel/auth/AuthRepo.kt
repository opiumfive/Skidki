package com.iterika.marvel.auth

import com.iterika.marvel.Prefs
import com.iterika.marvel.api.IApi
import com.iterika.marvel.api.enqueue

class AuthRepo(private val api: IApi, private val prefs: Prefs) {

    fun register(user: String, password: String, result: (AuthResult?) -> Unit) {
        api.register(user, password).enqueue({
            result.invoke(it.body())
        }, {
            result.invoke(null)
        }, {
            result.invoke(AuthResult(err = it?.err))
        })
    }

    fun login(user: String, password: String, result: (AuthResult?) -> Unit) {
        api.login(user, password).enqueue({
            result.invoke(it.body())
        }, {
            result.invoke(null)
        }, {
            result.invoke(AuthResult(err = it?.err))
        })
    }

    fun reset(email: String, result: (AuthResult?) -> Unit) {
        api.resetPassword(email).enqueue({
            result.invoke(it.body())
        }, {
            result.invoke(null)
        }, {
            result.invoke(AuthResult(err = it?.err))
        })
    }

    fun totalSaved(result: (TotalSaved?) -> Unit) {
        api.totalSaved(prefs.token).enqueue({
            result.invoke(it.body())
        }, {
            result.invoke(null)
        }, {
            result.invoke(TotalSaved(err = it?.err))
        })
    }
}