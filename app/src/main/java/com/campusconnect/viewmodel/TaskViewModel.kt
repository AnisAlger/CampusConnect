package com.campusconnect.viewmodel

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.data.model.Task
import com.campusconnect.data.repository.TaskRepository
import com.campusconnect.notifications.TaskReminderReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val repository: TaskRepository
) : AndroidViewModel(application) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.allTasks.collect {
                _tasks.value = it
            }
        }
    }

    // ✅ Insertion d'une tâche + notification
    fun insert(task: Task, reminderMinutes: Int) = viewModelScope.launch {
        val id = repository.insert(task) // DOIT retourner Long
        val savedTask = task.copy(id = id.toInt())
        if (savedTask.notify) {
            scheduleReminder(savedTask, reminderMinutes)
        }
    }

    fun update(task: Task, reminderMinutes: Int) = viewModelScope.launch {
        repository.update(task)
        if (task.notify) {
            scheduleReminder(task, reminderMinutes)
        } else {
            cancelReminder(task)
        }
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
        cancelReminder(task)
    }

    fun deleteAll() = viewModelScope.launch {
        _tasks.value.forEach { cancelReminder(it) }
        repository.deleteAll()
    }

    // ✅ Planifier une notification
    internal fun scheduleReminder(task: Task, offsetMinutes: Int) {
        val context = getApplication<Application>().applicationContext
        val reminderTime = task.dueDate - offsetMinutes * 60_000L
        if (reminderTime <= System.currentTimeMillis()) return

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("title", task.title)
            putExtra("description", task.description ?: "Tâche à faire")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }

    // ✅ Annuler une notification
    private fun cancelReminder(task: Task) {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
