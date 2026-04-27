package jobplace.courseproject.monitorstorm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jobplace.courseproject.monitorstorm.data.repository.StormRepository

class StormViewModelFactory(
    private val repo: StormRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StormViewModel(repo) as T
    }
}