package com.dmitrivenger.runo.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmitrivenger.runo.data.repository.RunRepository

class AnalyticsViewModelFactory(
    private val repository: RunRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AnalyticsViewModel(repository) as T
    }
}
