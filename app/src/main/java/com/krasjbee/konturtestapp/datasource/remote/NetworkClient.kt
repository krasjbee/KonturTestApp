package com.krasjbee.konturtestapp.datasource.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://raw.githubusercontent.com/"

object NetworkClient {

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }).build()


    val retrofit: Retrofit =
        Retrofit.Builder().client(client).baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

}