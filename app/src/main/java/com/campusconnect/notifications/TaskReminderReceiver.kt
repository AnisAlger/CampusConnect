package com.campusconnect.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.campusconnect.utils.NotificationHelper

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Tâche"
        val content = intent.getStringExtra("description") ?: "N'oublie pas cette tâche !"

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showNotification(context, title, content)
    }
}