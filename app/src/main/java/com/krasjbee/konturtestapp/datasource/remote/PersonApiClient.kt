package com.krasjbee.konturtestapp.datasource.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface PersonApiClient {

    @GET("/SkbkonturMobile/mobile-test-droid/master/json/{filename}")
    suspend fun getPersonList(@Path("filename") filename: String): Response<List<PersonRemote>>

    companion object {
        fun create(retrofit: Retrofit): PersonApiClient {
            return retrofit.create()
        }
    }

}