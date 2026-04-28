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
import jobplace.courseproject.monitorstorm.data.api.ApiProvider
import jobplace.courseproject.monitorstorm.data.db.DatabaseProvider
import jobplace.courseproject.monitorstorm.ui.MainActivity
import kotlinx.coroutines.flow.first
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
        val db = DatabaseProvider.get(applicationContext)
        val dao = db.kpDao()
        val repo = StormRepository(ApiProvider.api, dao)
        try {
            repo.refresh()
            val latest = dao.getLatestOne()?.value ?: 0.0
            updateWidget(applicationContext, latest)
            if (latest > 3) {
                showNotification("Магнитная буря", "Индекс магнитной бури = $latest",latest)
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(title: String, text: String,kp:Double) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags=Intent.FLAG_ACTIVITY_SINGLE_TOP;
            putExtra("kp",kp)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
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
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }

    private fun updateWidget(context: Context, kp: Double) {
        val manager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, StormWidget::class.java)
        val ids = manager.getAppWidgetIds(component)
        val status = when {
            kp < 5 -> "Спокойно"
            kp < 6 -> "Слабая буря"
            kp < 7 -> "Умеренная буря"
            kp < 8 -> "Сильная буря"
            kp < 9 -> "Экстремальная сильная буря"
            else -> "Сильная буря"
        }
        val bgRes = when {
            kp < 5 -> R.drawable.bg_green
            kp < 6 -> R.drawable.bg_yellow
            kp < 7 ->R.drawable.bg_yellowtransred
            kp < 8 ->R.drawable.bg_red
            kp < 9 ->R.drawable.bg_reddarkness
            else -> R.drawable.bg_magenta
        }
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            val intent = Intent(context, StormWidget::class.java).apply {
                action = "UPDATE_WIDGET"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.updateBtn, pendingIntent)
            views.setTextViewText(R.id.kpText, "Индекс м.б. : $kp")
            views.setTextViewText(R.id.statusText, status)
            views.setTextViewText(R.id.timeText, "Обновлено: $time")
            views.setInt(
                R.id.rootLayout,
                "setBackgroundResource",
              bgRes
            )
            manager.updateAppWidget(id, views)
        }

    }


}