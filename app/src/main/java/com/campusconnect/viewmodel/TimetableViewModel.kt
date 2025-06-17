package com.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.data.model.TimetableEntry
import com.campusconnect.data.repository.TimetableRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimetableViewModel(private val repository: TimetableRepository) : ViewModel() {

    val entries: StateFlow<List<TimetableEntry>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addOrUpdate(entry: TimetableEntry) {
        viewModelScope.launch {
            repository.insert(entry)
        }
    }

    fun delete(entry: TimetableEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
}
