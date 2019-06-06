package com.iterika.marvel.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class AuthViewModel(private val repo: AuthRepo) : ViewModel() {

    val authData = MutableLiveData<AuthResult>()
    val totalSavedData = MutableLiveData<TotalSaved>()

    fun login(user: String, pass: String) = repo.login(user, pass) { authData.value = it }

    fun register(user: String, pass: String) = repo.register(user, pass) { authData.value = it }

    fun restore(user: String) = repo.reset(user) { authData.value = it }

    fun totalSaved() = repo.totalSaved { totalSavedData.value = it }

}