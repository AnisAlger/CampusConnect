package com.campusconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    initialTask: Task?,
    onDismiss: () -> Unit,
    onSave: (Task, Int) -> Unit,
    defaultReminderMinutes: Int = 15
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    // States
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var reminderOffset by remember { mutableStateOf(initialTask?.reminderMinutes ?: defaultReminderMinutes) }
    var notify by remember { mutableStateOf(initialTask?.notify ?: true) }
    var isTitleError by remember { mutableStateOf(false) }
    var isDateError by remember { mutableStateOf(false) }

    // Calendar management - using derivedStateOf for better performance
    val calendarState = remember {
        val cal = Calendar.getInstance()
        cal.timeInMillis = initialTask?.dueDate ?: System.currentTimeMillis()
        // Ensure minimum time is now + 1 minute
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.timeInMillis = System.currentTimeMillis() + 60_000
        }
        cal
    }

    // Picker states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderMenu by remember { mutableStateOf(false) }

    // Formatters
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // Date Picker State
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendarState.timeInMillis,
        yearRange = IntRange(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.YEAR) + 1
        )
    )

    // Time Picker State
    val timePickerState = rememberTimePickerState(
        initialHour = calendarState.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendarState.get(Calendar.MINUTE),
        is24Hour = true
    )

    // Auto-focus title field when dialog opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTask == null) "Nouvelle tâche" else "Modifier tâche") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        isTitleError = false
                    },
                    label = { Text("Titre*") },
                    isError = isTitleError,
                    supportingText = {
                        if (isTitleError) Text("Champ obligatoire")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Spacer(Modifier.height(16.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // Date and Time selection
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Date Picker
                    Column(Modifier.weight(1f)) {
                        Text("Date d'échéance", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = dateFormatter.format(calendarState.time),
                            onValueChange = {},
                            readOnly = true,
                            isError = isDateError,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Time Picker
                    Column(Modifier.weight(1f)) {
                        Text("Heure d'échéance", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = timeFormatter.format(calendarState.time),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showTimePicker = true }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (isDateError) {
                    Text(
                        text = "La date doit être dans le futur",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Notification toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = notify,
                        onCheckedChange = { notify = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Activer les rappels")
                }

                // Reminder dropdown
                if (notify) {
                    Spacer(Modifier.height(16.dp))
                    Text("Délai de rappel avant échéance:")
                    Spacer(Modifier.height(8.dp))

                    Box(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = "$reminderOffset minutes",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showReminderMenu = true }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = showReminderMenu,
                            onDismissRequest = { showReminderMenu = false }
                        ) {
                            listOf(5, 10, 15, 30, 60, 120).forEach { minutes ->
                                DropdownMenuItem(
                                    text = { Text("$minutes minutes") },
                                    onClick = {
                                        reminderOffset = minutes
                                        showReminderMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validation
                    if (title.isBlank()) {
                        isTitleError = true
                        return@TextButton
                    }

                    // Update time from picker
                    calendarState.apply {
                        timeInMillis = datePickerState.selectedDateMillis ?: timeInMillis
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }

                    // Check if date is in future
                    if (calendarState.timeInMillis <= System.currentTimeMillis()) {
                        isDateError = true
                        return@TextButton
                    }

                    onSave(
                        Task(
                            id = initialTask?.id ?: 0,
                            title = title,
                            description = description,
                            subjectId = initialTask?.subjectId ?: 0,
                            dueDate = calendarState.timeInMillis,
                            notify = notify,
                            reminderMinutes = if (notify) reminderOffset else 0,
                        ),
                        if (notify) reminderOffset else 0
                    )
                }
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            showDatePicker = false
                            isDateError = false
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Sélectionner l'heure") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Annuler") }
            }
        )
    }
}