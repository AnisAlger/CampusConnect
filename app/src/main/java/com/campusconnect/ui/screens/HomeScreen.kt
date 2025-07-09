package com.campusconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusconnect.ui.components.*
import com.campusconnect.viewmodel.TaskViewModel
import com.campusconnect.viewmodel.EventViewModel
import com.campusconnect.viewmodel.TimetableViewModel
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import java.util.*

@Composable
fun HomeScreen(
    eventViewModel: EventViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    timetableViewModel: TimetableViewModel = viewModel()
) {
    val events = eventViewModel.events.collectAsState()
    val tasks = taskViewModel.tasks.collectAsState()
    val timetableEntries = timetableViewModel.entries.collectAsState()

    // Get current day and hour
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

    // Find current/next course
    val currentCourse = timetableEntries.value.firstOrNull {
        it.dayOfWeek == currentDay && it.startHour >= currentHour
    }
    val currentDayCoursesCount = timetableEntries.value.count { it.dayOfWeek == currentDay }
    val EventCardColor = Color(0xFFE3F2FD)  // Bleu clair doux
    val TaskCardColor = Color(0xFFE8F5E9)   // Vert clair doux
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Nouveau header amélioré
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column {
                // Première ligne avec "Bienvenue sur"
                Text(
                    text = "Bienvenue sur",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Deuxième ligne avec "CampusConnect" stylisé
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "Campus",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.primary,
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                offset = Offset(2f, 3f),
                                blurRadius = 8f
                            )
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "Connect",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                offset = Offset(2f, 3f),
                                blurRadius = 8f
                            )
                        )
                    )
                }

                // Ligne décorative
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        // Section Prochain cours avec carte améliorée
        SectionTitle("Votre prochain cours")
        if (currentCourse != null) {
            val dayNames = listOf("Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi")
            val dayName = dayNames.getOrElse(currentCourse.dayOfWeek) { "Aujourd'hui" }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(android.graphics.Color.parseColor(currentCourse.colorHex))
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Cours",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentCourse.type} ${currentCourse.label}",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Horaire",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$dayName • ${currentCourse.startHour}h - ${currentCourse.timeSlot}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        } else {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun cours prévu aujourd'hui",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Section Événements avec design amélioré
        SectionTitle("Vos événements à venir")
        if (events.value.isNotEmpty()) {
            val nextEvent = events.value.sortedBy { it.startTime }.first()

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = EventCardColor
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                EventItem(
                    event = nextEvent,
                    onClick = { println("Event clicked: ${nextEvent.title}") },
                    onDelete = { eventViewModel.deleteEvent(nextEvent) },
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = EventCardColor.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun événement à venir",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Tâches avec couleur personnalisée
        SectionTitle("Vos tâches à faire")
        if (tasks.value.isNotEmpty()) {
            val topTask = tasks.value
                .filter { !it.isDone }
                .sortedByDescending { it.priority }
                .first()

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = TaskCardColor
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                TaskItem(
                    task = topTask,
                    onToggleDone = { println("Task toggled: ${topTask.title}") },
                    onClick = { println("Task clicked: ${topTask.title}") },
                    onDelete = { taskViewModel.delete(topTask) },
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = TaskCardColor.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune tâche",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Section Statistiques avec design amélioré
        SectionTitle("Vos statistiques")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Cours aujourd'hui",
                value = currentDayCoursesCount.toString(),
                icon = Icons.Default.School,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Événements",
                value = events.value.size.toString(),
                icon = Icons.Default.Event,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Tâches",
                value = tasks.value.count { !it.isDone }.toString(),
                icon = Icons.Default.Task,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}