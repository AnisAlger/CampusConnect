package com.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.Task
import com.campusconnect.data.model.TaskPriority
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskItem(
    task: Task,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val priorityColor = when (task.priority) {
        TaskPriority.LOW -> Color(0xFFB0BEC5)
        TaskPriority.MEDIUM -> Color(0xFFFFC107)
        TaskPriority.HIGH -> Color(0xFFD32F2F)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone) Color(0xFFE0E0E0) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                    task.description?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Text(dateFormat.format(Date(task.dueDate)), style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = if (task.notify) "🔔 Rappel activé" else "🔕 Rappel désactivé",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (task.notify) Color.Green else Color.Gray
                    )
                }

                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red)
                }
            }
        }
    }
}

