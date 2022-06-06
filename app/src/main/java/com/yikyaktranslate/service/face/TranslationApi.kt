package com.yikyaktranslate.service.face

import com.yikyaktranslate.model.Language
import com.yikyaktranslate.model.api.requests.TranslateRequest
import com.yikyaktranslate.model.api.response.TranslateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TranslationApi {

    @GET("/languages")
    suspend fun getLanguages(): List<Language>

    @POST("/translate")
    suspend fun translate(@Body body: TranslateRequest): TranslateResponse

}