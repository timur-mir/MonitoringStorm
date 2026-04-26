package jobplace.courseproject.monitorstorm.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import jobplace.courseproject.monitorstorm.data.api.SpaceWeatherApi
import jobplace.courseproject.monitorstorm.data.db.AppDatabase
import jobplace.courseproject.monitorstorm.data.repository.StormRepository
import jobplace.courseproject.monitorstorm.ui.ui.theme.MonitorStormTheme
import jobplace.courseproject.monitorstorm.viewmodel.StormViewModel
import jobplace.courseproject.monitorstorm.worker.StormWorker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "storm_db"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://services.swpc.noaa.gov/json/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val repo = StormRepository(retrofit.create(SpaceWeatherApi::class.java), db.kpDao())
        val vm = StormViewModel(repo)

        setContent {
            StormScreen(vm)
        }

        startWorker()
    }

    private fun startWorker() {
        val work = PeriodicWorkRequestBuilder<StormWorker>(
            30, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "storm_worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                work
            )
    }
}
