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
import com.campusconnect.data.model.Event
import com.campusconnect.data.model.Task

@Composable
fun HomeScreen(

    eventViewModel: EventViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {

    // CRITICAL: Ensure 'EventViewModel' has a public StateFlow named 'events' or 'upcomingEvents'.
    // If it's named 'upcomingEvents' in your EventViewModel, then this line is correct:
    // val events = eventViewModel.upcomingEvents.collectAsState(initial = emptyList())
    // If it's named 'events', use this:
    val events = eventViewModel.events.collectAsState() // Using '.events' as a common convention

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

        }

        SectionTitle("À venir")
        if (events.value.isNotEmpty()) {
            events.value
                .sortedBy { it.startTime } // Assuming 'Event' has 'startTime'
                .take(1)
                .forEach { event ->
                    EventItem(
                        event = event,
                        onClick = {
                            // TODO: Implement event click action (e.g., navigation)
                            println("Event clicked: ${event.title}")
                        },
                        onDelete = {
                            // TODO: Implement event deletion (e.g., call eventViewModel.deleteEvent(event.id))
                            println("Delete event: ${event.title}")
                        }
                    )
                }
        } else {
            Text("Aucun événement à venir", style = MaterialTheme.typography.bodyMedium)
        }

        SectionTitle("Tâches prioritaires")
        if (tasks.value.isNotEmpty()) {
            tasks.value
                .filter { !it.isDone }
                .sortedByDescending { it.priority } // Assuming 'Task' has 'priority'
                .take(1)
                .forEach { task ->
                    TaskItem(
                        task = task,
                        onToggleDone = {
                            // TODO: Implement task toggle (e.g., taskViewModel.updateTask(task.copy(isDone = !task.isDone)))
                            println("Task toggled: ${task.title}, New status: ${!task.isDone}")
                        },
                        onClick = {
                            // TODO: Implement task click action (e.g., navigation)
                            println("Task clicked: ${task.title}")
                        }
                    )
                }
        } else {
            Text("Aucune tâche prioritaire", style = MaterialTheme.typography.bodyMedium)
        }

        SectionTitle("Statistiques rapides")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(title = "Heures", value = "5h") // TODO: Replace with dynamic data
            StatCard(title = "Tâches", value = tasks.value.count { !it.isDone }.toString())

        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
        modifier = Modifier.padding(top = 8.dp)
    )
}