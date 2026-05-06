package com.dmitrivenger.runo.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitrivenger.runo.data.repository.RunRepository
import com.dmitrivenger.runo.domain.model.Run
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(repository: RunRepository) : ViewModel() {
    val runs: StateFlow<List<Run>> = repository.allRuns.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
}
