package com.dmitrivenger.runo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmitrivenger.runo.data.preferences.UserPreferences
import com.dmitrivenger.runo.data.repository.RunRepository

class HomeViewModelFactory(
    private val repository: RunRepository,
    private val preferences: UserPreferences,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repository, preferences) as T
    }
}
