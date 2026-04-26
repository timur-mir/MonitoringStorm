package jobplace.courseproject.monitorstorm.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import jobplace.courseproject.monitorstorm.R
import jobplace.courseproject.monitorstorm.data.api.SpaceWeatherApi
import jobplace.courseproject.monitorstorm.data.db.AppDatabase
import jobplace.courseproject.monitorstorm.data.repository.StormRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StormWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "storm_db"
        ).build()
        val dao = db.kpDao()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://services.swpc.noaa.gov/json/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SpaceWeatherApi::class.java)
        val repo = StormRepository(api, dao)
        try {
            repo.refresh()
            val latest = api.getKp().last().kp_index
            if (latest > 4) {
                showNotification("Магнитная буря", "Индекс магнитной бури = $latest")
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(title: String, text: String) {
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "storm",
            "Storm Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, "storm")
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.storm)
            .build()

        manager.notify(1, notification)
    }
}