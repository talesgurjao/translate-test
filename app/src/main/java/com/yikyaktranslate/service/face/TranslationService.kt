package com.yikyaktranslate.service.face

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yikyaktranslate.model.api.ApiError
import com.yikyaktranslate.model.api.ApiResult
import com.yikyaktranslate.model.api.requests.TranslateRequest
import com.yikyaktranslate.model.api.response.ApiErrorResponse
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

object TranslationService {

    private const val BASE_URL =
        "https://libretranslate.de/" // this official mirror site doesn't require api key

    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TranslationApi::class.java)
    }

    private fun parseError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) {
            return null
        }
        val adapter = moshi.adapter(ApiErrorResponse::class.java)
        return adapter.fromJson(errorBody)?.error
    }

    private inline fun <reified T> safeCall(call: TranslationApi.() -> T) =
        try {
            val response = call(api)
            ApiResult(response, null)
        } catch (e: Exception) {
            val error = when (e) {
                is IOException -> ApiError.Network(e.message)
                is HttpException -> ApiError.Http(parseError(e.response()?.errorBody()?.string()))
                else -> ApiError.Unknown(e.message)
            }
            ApiResult(null, error)
        }

    suspend fun getLanguages() = safeCall { getLanguages() }

    suspend fun translate(text: String, source: String, target: String) =
        safeCall { translate(TranslateRequest(text, source, target)) }
}