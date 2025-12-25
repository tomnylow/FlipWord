package com.tomnylow.flipword.data.remote

import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApi {
    @GET("translate_a/single")
    suspend fun translate(
        @Query("q") text: String,
        @Query("sl") from: String = "en",
        @Query("tl") to: String = "ru",
        @Query("client") client: String = "gtx",
        @Query("dt") dt: String = "t"
    ): JsonElement
}
/* Пример ответа:
    [
        [
            ["привет","hello",null,null,10]
        ],
        null,
        "en",
        null,
        null,
        null,
        []
    ]
 */
