package com.yikyaktranslate.model.api.requests

data class TranslateRequest(
    val q: String,
    val source: String,
    val target: String
)
