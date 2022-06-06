package com.yikyaktranslate.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yikyaktranslate.R
import com.yikyaktranslate.model.Language
import com.yikyaktranslate.model.api.ApiResult
import com.yikyaktranslate.service.face.TranslationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class TranslateViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceLanguageCode: String = application.getString(R.string.source_language_code)

    val languages by lazy {
        flow {
            val response = TranslationService.getLanguages()
            if (response.success) {
                val languages = response.data ?: emptyList()
                selectedLanguage.emit(languages.firstOrNull())
                emit(languages)
            } else {
                handleGenericError(response)
            }
        }.flowOn(Dispatchers.IO)
    }

    val error = MutableStateFlow<String?>(null)
    val selectedLanguage = MutableStateFlow<Language?>(null)
    val translatedText = MutableStateFlow("")

    private fun handleGenericError(response: ApiResult<*>) {
        viewModelScope.launch {
            error.emit(response.error?.message)
        }
    }

    /**
     * Updates the selected target language when the user selects a new language
     *
     * @param newLanguage The new language
     */
    fun onTargetLanguageChange(newLanguage: Language) {
        viewModelScope.launch {
            selectedLanguage.emit(newLanguage)
        }
    }

    /**
     * Translates the text
     * @param text The text to translate
     */
    fun translate(text: String) {
        viewModelScope.launch {
            val target = selectedLanguage.firstOrNull()?.code ?: return@launch
            val response = TranslationService.translate(text, sourceLanguageCode, target)
            if (response.success) {
                val result = response.data?.translatedText ?: ""
                translatedText.emit(result)
            } else {
                handleGenericError(response)
            }
        }
    }

}