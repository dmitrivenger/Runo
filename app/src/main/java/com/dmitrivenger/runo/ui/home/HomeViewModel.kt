package com.dmitrivenger.runo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitrivenger.runo.data.preferences.UserPreferences
import com.dmitrivenger.runo.data.repository.RunRepository
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.domain.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RunRepository,
    preferences: UserPreferences,
) : ViewModel() {

    val profile: StateFlow<UserProfile> = preferences.userProfile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile()
    )

    val runs: StateFlow<List<Run>> = repository.allRuns.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun deleteRun(id: Long) {
        viewModelScope.launch { repository.deleteRun(id) }
    }
}
