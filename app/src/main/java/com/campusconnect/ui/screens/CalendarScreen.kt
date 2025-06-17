package com.campusconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campusconnect.data.model.TimetableEntry
import com.campusconnect.viewmodel.TimetableViewModel

@Composable
fun CalendarScreen(viewModel: TimetableViewModel = viewModel()) {
    val days = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi")
    val hours = (8..18).toList()

    val entries by viewModel.entries.collectAsState(initial = emptyList())

    var editingEntry by remember { mutableStateOf<TimetableEntry?>(null) }
    var subjectInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(8.dp)) {
        Row {
            Spacer(modifier = Modifier.width(60.dp)) // Pour les heures
            days.forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f).padding(4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        hours.forEach { hour ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$hour h",
                    modifier = Modifier.width(60.dp).padding(4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
                for (dayIndex in 0 until 6) {
                    val entry = entries.find { it.dayOfWeek == dayIndex && it.hour == hour }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(0.5.dp, Color.Gray)
                            .clickable {
                                editingEntry = entry ?: TimetableEntry(dayOfWeek = dayIndex, hour = hour, label = "")
                                subjectInput = editingEntry?.label ?: ""
                            }
                            .background(if (entry != null) Color(0xFFB3E5FC) else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(entry?.label ?: "")
                    }
                }
            }
        }
    }

    editingEntry?.let { entry ->
        AlertDialog(
            onDismissRequest = { editingEntry = null },
            title = { Text("Modifier le cours") },
            text = {
                Column {
                    Text("Jour: ${days[entry.dayOfWeek]}, Heure: ${entry.hour}h")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        label = { Text("Nom du cours (CM/TD/TP)") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.insert(entry.copy(label = subjectInput))
                    editingEntry = null
                }) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                if (entry.label.isNotBlank()) {
                    OutlinedButton(onClick = {
                        viewModel.delete(entry)
                        editingEntry = null
                    }) {
                        Text("Supprimer")
                    }
                } else {
                    TextButton(onClick = { editingEntry = null }) {
                        Text("Annuler")
                    }
                }
            }
        )
    }
}
