package com.vaalzebub.fintatechtest.di


import android.content.Context
import android.content.SharedPreferences
import com.vaalzebub.fintatechtest.BuildConfig
import com.vaalzebub.fintatechtest.data.FinApi
import com.vaalzebub.fintatechtest.domain.repository.ApiRepository
import com.vaalzebub.fintatechtest.domain.repository.SocketClient
import com.vaalzebub.fintatechtest.domain.usecase.ApiUseCase
import com.vaalzebub.fintatechtest.domain.utils.AuthInterceptor
import com.vaalzebub.fintatechtest.domain.utils.SessionManager
import com.vaalzebub.fintatechtest.presentation.MainViewModel

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun loadModule() = module {
    factory<SharedPreferences> { androidContext().getSharedPreferences("", Context.MODE_PRIVATE) }
    single<SessionManager> { SessionManager(get()) }
    factory<AuthInterceptor> { AuthInterceptor(get()) }
    single<FinApi> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URI)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                getOkHttpClient(get())
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS).build()
            )
            .build().create(FinApi::class.java)
    }
    factory<ApiRepository> { ApiRepository(get(), get()) }
    factory<ApiUseCase> { ApiUseCase(get(), get()) }
    single<SocketClient> { SocketClient(get()) }
    viewModel<MainViewModel>{MainViewModel(get(), get())}

}

private fun getOkHttpClient(authInterceptor:AuthInterceptor) = OkHttpClient()
    .newBuilder()
    .addInterceptor(authInterceptor)
    .addInterceptor(
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    )