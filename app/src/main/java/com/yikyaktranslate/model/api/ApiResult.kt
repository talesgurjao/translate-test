package com.yikyaktranslate.model.api

data class ApiResult<T>(val data: T?, val error: ApiError?) {
    val success by lazy { error == null }
}