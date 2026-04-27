package jobplace.courseproject.monitorstorm.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import jobplace.courseproject.monitorstorm.data.api.ApiProvider
import jobplace.courseproject.monitorstorm.data.api.SpaceWeatherApi
import jobplace.courseproject.monitorstorm.data.db.AppDatabase
import jobplace.courseproject.monitorstorm.data.db.DatabaseProvider
import jobplace.courseproject.monitorstorm.data.repository.StormRepository
import jobplace.courseproject.monitorstorm.ui.ui.theme.MonitorStormTheme
import jobplace.courseproject.monitorstorm.viewmodel.StormViewModel
import jobplace.courseproject.monitorstorm.viewmodel.StormViewModelFactory
import jobplace.courseproject.monitorstorm.worker.StormWorker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var repo: StormRepository
    private val vm: StormViewModel by viewModels {
        StormViewModelFactory(repo)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseProvider.get(applicationContext)
        val dao = db.kpDao()
        repo = StormRepository(ApiProvider.api, dao)
        setContent {
            MonitorStormTheme() {
                Surface(modifier = Modifier.fillMaxSize()) {
                    StormScreen(vm)
                }
            }
        }
    }
}
