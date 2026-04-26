package jobplace.courseproject.monitorstorm.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jobplace.courseproject.monitorstorm.data.model.KpIndex
import jobplace.courseproject.monitorstorm.data.repository.StormRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StormViewModel(repo: StormRepository) : ViewModel() {
    init {
        viewModelScope.launch {
            repo.refresh()
        }
    }
    val data = repo.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}