package jobplace.courseproject.monitorstorm

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import jobplace.courseproject.monitorstorm.worker.StormWorker
import java.util.concurrent.TimeUnit

class StormWidget : AppWidgetProvider() {
    companion object {
        const val ACTION_UPDATE = "UPDATE_WIDGET"
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val intent = Intent(context, StormWidget::class.java).apply {
                action = ACTION_UPDATE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                widgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.updateBtn, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<StormWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "storm_update",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWork
        )
    }
    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        val work = OneTimeWorkRequestBuilder<StormWorker>().build()
        WorkManager.getInstance(context).enqueue(work)
    }
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context)
            .cancelUniqueWork("storm_update")
    }
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_UPDATE) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val work = OneTimeWorkRequestBuilder<StormWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(work)
        }
    }
}