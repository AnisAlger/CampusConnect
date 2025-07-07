package com.campusconnect.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusconnect.ui.components.* // Make sure StatCard, EventItem, SubjectChip, TaskItem are here
import com.campusconnect.viewmodel.TaskViewModel
import com.campusconnect.viewmodel.EventViewModel
// Import your data models if needed for lambdas, e.g., for task.id

@Composable
fun HomeScreen(
    eventViewModel: EventViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {
    val events = eventViewModel.events.collectAsState()
    val tasks = taskViewModel.tasks.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Bienvenue sur CampusConnect !", style = MaterialTheme.typography.headlineMedium)

        SectionTitle("Matières")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TODO: Add SubjectChip items here if you have a list of subjects
            // Example:
            // val subjects = listOf("Maths", "Physique", "Info")
            // subjects.forEach { subjectName ->
            //    SubjectChip(name = subjectName, onClick = { /* TODO */ })
            // }
        }

        SectionTitle("Événements")
        if (events.value.isNotEmpty()) {
            events.value
                .sortedBy { it.startTime } // Assuming 'Event' has 'startTime'
                .take(1) // Display only the next upcoming event
                .forEach { event ->
                    EventItem(
                        event = event,
                        onClick = {
                            // TODO: Implement event click action (e.g., navigation to event details)
                            println("Event clicked: ${event.title}")
                        },
                        onDelete = {
                            eventViewModel.deleteEvent(event) // Assuming you have a deleteEvent function in EventViewModel
                        }
                    )
                }
        } else {
            Text("Aucun événement à venir", style = MaterialTheme.typography.bodyMedium)
        }

        SectionTitle("Tâches prioritaires")
        if (tasks.value.isNotEmpty()) {
            tasks.value
                .filter { !it.isDone } // Show only incomplete tasks
                .sortedByDescending { it.priority } // Assuming 'Task' has 'priority'
                .take(1) // Display only the highest priority task
                .forEach { task ->
                    TaskItem(
                        task = task,
                        onToggleDone = {
                            // TODO: Implement task toggle
                            // Make sure your TaskViewModel has an updateTask or toggleTaskDone function
                            // val updatedTask = task.copy(isDone = !task.isDone)
                            // taskViewModel.updateTask(updatedTask)
                            println("Task toggled: ${task.title}, New status: ${!task.isDone}")
                        },
                        onClick = {
                            // TODO: Implement task click action (e.g., navigation to task details or edit)
                            println("Task clicked: ${task.title}")
                        },
                        // CORRECTION APPLIQUÉE ICI :
                        onDelete = {
                            taskViewModel.delete(task)
                        }
                    )
                }
        } else {
            Text("Aucune tâche prioritaire", style = MaterialTheme.typography.bodyMedium)
        }

        SectionTitle("Statistiques rapides")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Emplois",
                value = events.value.size.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Événements",
                value = events.value.size.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Tâches",
                value = tasks.value.count { !it.isDone }.toString(),
                modifier = Modifier.weight(1f)
            )
        }

    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp) // Added bottom padding for spacing
    )
}