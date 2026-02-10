package com.tomnylow.flipword.data.remote

import retrofit2.http.GET

interface RandomApi {
    @GET("word")
    suspend fun getWord(): List<String>
}