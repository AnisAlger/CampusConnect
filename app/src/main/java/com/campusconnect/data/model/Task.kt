package com.campusconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val subjectId: Int, // Relation avec Subject
    val dueDate: Long, // Timestamp
    val isDone: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val notify: Boolean = true,
    val reminderMinutes: Int = 15 // <<< ADD THIS LINE (or your preferred default)
)



enum class TaskPriority {
    LOW, MEDIUM, HIGH
}