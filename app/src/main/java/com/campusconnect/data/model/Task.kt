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
    val priority: TaskPriority = TaskPriority.MEDIUM
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}
