package com.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusconnect.data.model.Task
import com.campusconnect.data.model.TaskPriority
import com.campusconnect.ui.theme.*
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

    // States
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var priority by remember { mutableStateOf(initialTask?.priority ?: TaskPriority.MEDIUM) }
    var reminderOffset by remember { mutableStateOf(initialTask?.reminderMinutes ?: defaultReminderMinutes) }
    var notify by remember { mutableStateOf(initialTask?.notify ?: true) }
    var isTitleError by remember { mutableStateOf(false) }

    // Calendar management
    val calendarState = remember {
        val cal = Calendar.getInstance()
        cal.timeInMillis = initialTask?.dueDate ?: (System.currentTimeMillis() + 3600000L) // Default +1 hour
        cal
    }

    // Picker states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderMenu by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var selectedDateText by remember { mutableStateOf(dateFormatter.format(calendarState.time)) }
    var selectedTimeText by remember { mutableStateOf(timeFormatter.format(calendarState.time)) }

    // Date Picker State
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendarState.timeInMillis,
        yearRange = IntRange(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.YEAR) + 2
        )
    )

    // Time Picker State
    val timePickerState = rememberTimePickerState(
        initialHour = calendarState.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendarState.get(Calendar.MINUTE),
        is24Hour = true
    )

    LaunchedEffect(Unit) {
        if (initialTask == null) {
            focusRequester.requestFocus()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = if (initialTask == null) "Nouvelle tâche" else "Modifier la tâche",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        isTitleError = false
                    },
                    label = { Text("Titre de la tâche *") },
                    isError = isTitleError,
                    supportingText = {
                        if (isTitleError) Text("Le titre est requis", color = MaterialTheme.colorScheme.error)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnelle)") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Selection
                Column {
                    Text(
                        text = "Priorité",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(TaskPriority.LOW, "Basse", PriorityLowColor),
                            Triple(TaskPriority.MEDIUM, "Moyenne", PriorityMediumColor),
                            Triple(TaskPriority.HIGH, "Haute", PriorityHighColor)
                        ).forEach { (p, label, color) ->
                            val isSelected = priority == p
                            FilterChip(
                                selected = isSelected,
                                onClick = { priority = p },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(label)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Date and Time selection
                Column {
                    Text(
                        text = "Échéance",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Date Button
                        OutlinedCard(
                            onClick = { showDatePicker = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedDateText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }

                        // Time Button
                        OutlinedCard(
                            onClick = { showTimePicker = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedTimeText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Notification toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Activer un rappel",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Switch(
                        checked = notify,
                        onCheckedChange = { notify = it }
                    )
                }

                // Reminder dropdown
                if (notify) {
                    Box(Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            onClick = { showReminderMenu = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Rappeler $reminderOffset min avant",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = showReminderMenu,
                            onDismissRequest = { showReminderMenu = false }
                        ) {
                            listOf(5, 10, 15, 30, 60, 120).forEach { minutes ->
                                DropdownMenuItem(
                                    text = { Text("$minutes minutes avant") },
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
            Button(
                onClick = {
                    if (title.isBlank()) {
                        isTitleError = true
                        return@Button
                    }

                    // Update time from picker
                    calendarState.apply {
                        datePickerState.selectedDateMillis?.let {
                            timeInMillis = it
                        }
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }

                    onSave(
                        Task(
                            id = initialTask?.id ?: 0,
                            title = title.trim(),
                            description = description.trim().ifEmpty { null },
                            subjectId = initialTask?.subjectId ?: 0,
                            dueDate = calendarState.timeInMillis,
                            isDone = initialTask?.isDone ?: false,
                            priority = priority,
                            notify = notify,
                            reminderMinutes = if (notify) reminderOffset else 0
                        ),
                        if (notify) reminderOffset else 0
                    )
                },
                shape = RoundedCornerShape(12.dp)
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
                            val temp = Calendar.getInstance().apply { timeInMillis = it }
                            selectedDateText = dateFormatter.format(temp.time)
                        }
                        showDatePicker = false
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
            title = { Text("Sélectionner l'heure", style = MaterialTheme.typography.titleMedium) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val temp = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        selectedTimeText = timeFormatter.format(temp.time)
                        showTimePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Annuler") }
            }
        )
    }
}