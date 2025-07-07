package com.campusconnect.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.campusconnect.R

class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val taskTitle = inputData.getString("title") ?: "Tâche à venir"
        val taskDescription = inputData.getString("description") ?: ""

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "task_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Rappels de tâche",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Rappel : $taskTitle")
            .setContentText(taskDescription)
            .setSmallIcon(R.drawable.ic_notification) // remplace avec ton icône
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify((0..100000).random(), notification)

        return Result.success()
    }
}
