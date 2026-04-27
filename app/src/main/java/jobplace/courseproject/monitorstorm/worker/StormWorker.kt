package jobplace.courseproject.monitorstorm.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import jobplace.courseproject.monitorstorm.R
import jobplace.courseproject.monitorstorm.data.api.SpaceWeatherApi
import jobplace.courseproject.monitorstorm.data.db.AppDatabase
import jobplace.courseproject.monitorstorm.data.repository.StormRepository
import jobplace.courseproject.monitorstorm.StormWidget
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            updateWidget(applicationContext,latest)
            if (latest > 0) {
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
    private fun updateWidget(context: Context, kp: Double) {

        val manager = AppWidgetManager.getInstance(context)

        val component = ComponentName(context, StormWidget::class.java)

        val ids = manager.getAppWidgetIds(component)
        val status = when {
            kp < 4 -> "Спокойно"
            kp < 6 -> "Умеренная буря"
            else -> "Сильная буря"
        }

        val bgColor = when {
            kp < 4 -> "#2E7D32" // зелёный
            kp < 6 -> "#F9A825" // жёлтый
            else -> "#C62828" // красный
        }

        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        for (id in ids) {
            views.setTextViewText(R.id.kpText, "Индекс м.б. : $kp")
            views.setTextViewText(R.id.statusText, status)
            views.setTextViewText(R.id.timeText, "Обновлено: $time")
            manager.updateAppWidget(id, views)
        }
        val intent = Intent(context, StormWidget::class.java).apply {
            action = "UPDATE_WIDGET"
        }
        //intent.setPackage("jobplace.courseproject.monitorstorm")

        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.updateBtn, pendingIntent)
    }


}