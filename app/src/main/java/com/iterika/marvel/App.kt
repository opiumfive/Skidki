package com.iterika.marvel

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.iterika.marvel.api.IApi
import com.iterika.marvel.auth.AuthRepo
import com.iterika.marvel.auth.AuthViewModel
import com.iterika.marvel.cart.CartHolder
import com.iterika.marvel.cart.CartViewModel
import com.iterika.marvel.catalog.CatalogRepo
import com.iterika.marvel.catalog.CatalogViewModel
import com.iterika.marvel.coupons.CouponsRepo
import com.iterika.marvel.coupons.CouponsViewModel
import com.iterika.marvel.utils.DateFormatter
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.terrakok.cicerone.Cicerone
import java.util.concurrent.TimeUnit

class App : Application() {

    // DI
    val appModule: Module = module {

        viewModel { CatalogViewModel(get(), get()) }

        viewModel { AuthViewModel(get()) }

        viewModel { CouponsViewModel(get()) }

        viewModel { CartViewModel(get()) }

        single { CatalogRepo(get()) }

        single { CouponsRepo(get(), get(), get()) }

        factory { AuthRepo(get(), get()) }

        single { DateFormatter() }

        single { CartHolder() }

        single { Prefs(get()) }

        single { Cicerone.create() } // navigator

        single<IApi> {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(get())
                .addConverterFactory(GsonConverterFactory.create(get()))
                .build()
                .create(IApi::class.java)
        }

        factory { GsonBuilder().create() }

        factory {
            OkHttpClient().newBuilder()
                .addInterceptor(ChuckInterceptor(androidContext()).apply { showNotification(BuildConfig.DEBUG) })
                .connectTimeout(TIMEOUT_REQUEST, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_REQUEST, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }

        factory<SharedPreferences> { androidContext().getSharedPreferences("prefs", 0) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}