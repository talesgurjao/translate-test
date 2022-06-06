package com.yikyaktranslate.model.api

sealed class ApiError(val message: String?) {
    class Unknown(message: String?) : ApiError(message)
    class Network(message: String?) : ApiError(message)
    class Http(message: String?) : ApiError(message)
}
