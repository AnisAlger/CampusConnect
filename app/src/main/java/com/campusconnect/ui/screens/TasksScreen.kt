package com.campusconnect.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusconnect.data.model.Task
import com.campusconnect.data.model.TaskPriority
import com.campusconnect.ui.components.TaskDialog
import com.campusconnect.ui.components.TaskItem
import com.campusconnect.ui.theme.PriorityHighColor
import com.campusconnect.ui.theme.PriorityLowColor
import com.campusconnect.viewmodel.TaskViewModel

enum class TaskFilter {
    ALL, PENDING, COMPLETED, HIGH_PRIORITY
}

@Composable
fun TasksScreen(taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.tasks.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf(TaskFilter.ALL) }

    val totalCount = tasks.size
    val completedCount = tasks.count { it.isDone }
    val pendingCount = totalCount - completedCount
    val highPriorityCount = tasks.count { it.priority == TaskPriority.HIGH && !it.isDone }

    val completionProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    // Filter tasks based on filter chip & search query
    val filteredTasks = remember(tasks, currentFilter, searchQuery) {
        tasks.filter { task ->
            val matchesFilter = when (currentFilter) {
                TaskFilter.ALL -> true
                TaskFilter.PENDING -> !task.isDone
                TaskFilter.COMPLETED -> task.isDone
                TaskFilter.HIGH_PRIORITY -> task.priority == TaskPriority.HIGH && !task.isDone
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                task.title.contains(searchQuery, ignoreCase = true) ||
                        (task.description?.contains(searchQuery, ignoreCase = true) == true)
            }
            matchesFilter && matchesSearch
        }.sortedWith(
            compareBy<Task> { it.isDone }
                .thenByDescending { it.priority }
                .thenBy { it.dueDate }
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingTask = null
                    showDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nouvelle tâche", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tâches & Projets",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (pendingCount > 0) "$pendingCount tâches restantes à accomplir" else "Toutes les tâches sont terminées !",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Completion Progress Card
            if (totalCount > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progression globale",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$completedCount/$totalCount (${(completionProgress * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { completionProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher une tâche...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = currentFilter == TaskFilter.ALL,
                    onClick = { currentFilter = TaskFilter.ALL },
                    label = { Text("Toutes ($totalCount)") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = currentFilter == TaskFilter.PENDING,
                    onClick = { currentFilter = TaskFilter.PENDING },
                    label = { Text("À faire ($pendingCount)") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = currentFilter == TaskFilter.COMPLETED,
                    onClick = { currentFilter = TaskFilter.COMPLETED },
                    label = { Text("Faites ($completedCount)") },
                    shape = RoundedCornerShape(10.dp)
                )
                if (highPriorityCount > 0) {
                    FilterChip(
                        selected = currentFilter == TaskFilter.HIGH_PRIORITY,
                        onClick = { currentFilter = TaskFilter.HIGH_PRIORITY },
                        label = { Text("Urgentes ($highPriorityCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PriorityHighColor.copy(alpha = 0.15f),
                            selectedLabelColor = PriorityHighColor
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tasks List
            if (filteredTasks.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggleDone = { taskViewModel.toggleDone(task) },
                            onDelete = { taskViewModel.delete(task) },
                            onClick = {
                                editingTask = task
                                showDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentFilter == TaskFilter.COMPLETED) Icons.Default.Checklist
                                else Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty()) "Aucun résultat trouvé"
                            else if (currentFilter == TaskFilter.COMPLETED) "Aucune tâche terminée pour l'instant"
                            else "Aucune tâche à afficher",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty()) "Essayez une autre recherche."
                            else "Créez une tâche pour organiser votre travail étudiant !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                editingTask = null
                                showDialog = true
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ajouter une tâche")
                        }
                    }
                }
            }
        }
    }

    // Task Create / Edit Dialog
    if (showDialog) {
        TaskDialog(
            initialTask = editingTask,
            onDismiss = {
                showDialog = false
                editingTask = null
            },
            onSave = { task, reminderOffset ->
                if (editingTask != null) {
                    taskViewModel.update(task, reminderOffset)
                } else {
                    taskViewModel.insert(task, reminderOffset)
                }
                showDialog = false
                editingTask = null
            }
        )
    }
}