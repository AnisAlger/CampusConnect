package com.campusconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campusconnect.viewmodel.TaskViewModel
import com.campusconnect.ui.components.TaskItem
import com.campusconnect.ui.components.TaskDialog

@Composable
fun TasksScreen(taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mes tâches", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        tasks.forEach { task ->
            TaskItem(
                task = task,
                onToggleDone = { /* Gestion état terminé */ },
                onDelete = { taskViewModel.delete(task) },
                onClick = { /* Navigation ou édition */ },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Boîte de dialogue pour ajouter une tâche
    if (showDialog) {
        TaskDialog(
            initialTask = null,
            onDismiss = { showDialog = false },
            onSave = { task, reminderOffset ->
                taskViewModel.insert(task, reminderOffset)
                showDialog = false
            },
            defaultReminderMinutes = 15 // Valeur par défaut mais modifiable par l'utilisateur
        )
    }

    // Bouton d'ajout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter une tâche")
        }
    }
}