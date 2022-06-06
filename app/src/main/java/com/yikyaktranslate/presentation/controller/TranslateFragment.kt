package com.yikyaktranslate.presentation.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.yikyaktranslate.presentation.theme.YikYakTranslateTheme
import com.yikyaktranslate.presentation.view.TranslateView
import com.yikyaktranslate.presentation.viewmodel.TranslateViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TranslateFragment : Fragment() {

    private val translateViewModel: TranslateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                YikYakTranslateTheme {
                    val languages by translateViewModel.languages.collectAsState(initial = listOf())
                    val targetLanguage by translateViewModel.selectedLanguage.collectAsState(null)
                    val translatedText by translateViewModel.translatedText.collectAsState("")

                    // Create Compose view
                    TranslateView(
                        languages = languages,
                        targetLanguage = targetLanguage,
                        onTargetLanguageSelected = translateViewModel::onTargetLanguageChange,
                        onTranslateClick = translateViewModel::translate,
                        translatedText = translatedText
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            translateViewModel.error.collectLatest { message ->
                if (!message.isNullOrBlank()) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}