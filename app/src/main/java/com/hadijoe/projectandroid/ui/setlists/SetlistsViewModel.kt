package com.hadijoe.projectandroid.ui.setlists

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hadijoe.projectandroid.data.AppDatabase
import com.hadijoe.projectandroid.data.SetlistRepository
import com.hadijoe.projectandroid.model.Setlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SetlistsViewModel(private val repository: SetlistRepository) : ViewModel() {

    val setlists: StateFlow<List<Setlist>> = repository.setlists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // The name typed in the "new setlist" field.
    private val _newName = MutableStateFlow("")
    val newName: StateFlow<String> = _newName.asStateFlow()

    fun onNewNameChange(value: String) {
        _newName.value = value
    }

    fun addSetlist() {
        val name = _newName.value.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addSetlist(name)
        }
        _newName.value = ""
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val database = AppDatabase.getInstance(application)
                SetlistsViewModel(SetlistRepository(database.setlistDao()))
            }
        }
    }
}
