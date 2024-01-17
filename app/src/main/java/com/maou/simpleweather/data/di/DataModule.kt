package com.maou.simpleweather.data.di

import com.maou.simpleweather.BuildConfig
import com.maou.simpleweather.data.controller.LocationController
import com.maou.simpleweather.data.repository.WeatherRepository
import com.maou.simpleweather.data.service.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

val dataModule = module {
    singleOf(::WeatherRepository)

    single {
        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            ).build()

        retrofit.create(ApiService::class.java)
    }

    singleOf(::LocationController)
}