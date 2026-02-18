package com.tk.a12testers14days.di

import com.tk.a12testers14days.data.remote.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        val gson = com.google.gson.GsonBuilder()
            .registerTypeAdapter(com.tk.a12testers14days.data.remote.UserDto::class.java, com.tk.a12testers14days.data.remote.UserDtoDeserializer())
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl("http://192.168.1.21:5001/api/") // Local IP for physical device
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}
