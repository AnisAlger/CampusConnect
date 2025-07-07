package com.campusconnect.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Keep this import
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.Task
import com.campusconnect.data.model.TaskPriority
import java.util.*

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }

    // Get the context from the composable scope
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val task = Task(
                    title = title,
                    description = description,
                    subjectId = 0, // Consider making this configurable
                    dueDate = selectedDate,
                    priority = priority,
                    // reminderMinutes = ... // If you have this property in Task
                )
                onSave(task)
            }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = { Text("Nouvelle tâche") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text("Priorité :", modifier = Modifier.alignByBaseline())
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuPriority(priority) { newPriority -> priority = newPriority }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // You might want to display the selected date/time here
                // e.g., Text(SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(selectedDate)))
                // Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val now = Calendar.getInstance()
                    DatePickerDialog(
                        context, // Use the stored context
                        { _, year, month, day ->
                            TimePickerDialog(
                                context, // Use the stored context
                                { _, hour, minute ->
                                    val cal = Calendar.getInstance()
                                    cal.set(year, month, day, hour, minute)
                                    selectedDate = cal.timeInMillis
                                },
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                true // is24HourView
                            ).show()
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    // This button's text "Rappel avant échéance" might be misleading
                    // as it's currently setting the due date/time, not a reminder offset.
                    // Consider changing it to "Choisir Date et Heure" or similar.
                    Text("Choisir Date et Heure")
                }
            }
        }
    )
}

@Composable
fun DropdownMenuPriority(selected: TaskPriority, onSelect: (TaskPriority) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected.name)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TaskPriority.values().forEach { priorityValue -> // Renamed 'it' for clarity
                DropdownMenuItem(
                    text = { Text(priorityValue.name) },
                    onClick = {
                        onSelect(priorityValue)
                        expanded = false
                    }
                )
            }
        }
    }
}