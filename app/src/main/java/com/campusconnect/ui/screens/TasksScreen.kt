package com.campusconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.Task // Your Task data class
import com.campusconnect.data.model.TaskPriority // Your TaskPriority enum
import com.campusconnect.ui.components.TaskItem

@Composable
fun TasksScreen() {
    val sampleTasks = remember {
        mutableStateListOf(
            Task(
                id = 1,
                title = "Réviser JEE",
                description = "Servlets et JSP",
                subjectId = 101, // Provide an actual Int for subjectId
                dueDate = System.currentTimeMillis() + 86400000L, // Ensure this is a Long
                priority = TaskPriority.HIGH
                // isDone will use its default value (false)
            ),
            Task(
                id = 2,
                title = "TP Android",
                description = "Jetpack Compose",
                subjectId = 102, // Provide an actual Int for subjectId
                dueDate = System.currentTimeMillis() + 172800000L, // Ensure this is a Long
                priority = TaskPriority.MEDIUM
                // isDone will use its default value (false)
            )
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mes tâches", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        sampleTasks.forEachIndexed { index, task ->
            TaskItem(
                task = task,
                onToggleDone = {
                    val updatedTask = task.copy(isDone = !task.isDone)
                    sampleTasks[index] = updatedTask
                },
                onClick = { /* ouvrir détails */ }
            )
        }
    }
}