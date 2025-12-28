package com.tomnylow.flipword.data.remote

import com.tomnylow.flipword.data.remote.dto.DictionaryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    @GET("api/v2/entries/en/{word}")
    suspend fun getDefinition(@Path("word") word: String): List<DictionaryDto>
}
