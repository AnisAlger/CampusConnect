package com.campusconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.TimetableEntry
import com.campusconnect.ui.components.TimetableDialog
import com.campusconnect.viewmodel.TimetableViewModel

@Composable
fun CalendarScreen(viewModel: TimetableViewModel) {
    val entries by viewModel.entries.collectAsState()
    val days = listOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
    val hours = (8..18).toList()

    var editingEntry by remember { mutableStateOf<TimetableEntry?>(null) }

    Column {
        Row {
            Spacer(modifier = Modifier.width(48.dp))
            days.forEach {
                Text(text = it, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            }
        }

        hours.forEach { hour ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$hour h", modifier = Modifier.width(48.dp))
                for (day in 0..6) {
                    val entry = entries.find { it.dayOfWeek == day && it.hour == hour }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(2.dp)
                            .background(Color(android.graphics.Color.parseColor(entry?.colorHex ?: "#FFFFFF")))
                            .clickable {
                                editingEntry = entry ?: TimetableEntry(dayOfWeek = day, hour = hour, label = "")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = entry?.label ?: "")
                    }
                }
            }
        }
    }

    editingEntry?.let {
        TimetableDialog(
            entry = it,
            onSave = {
                viewModel.addOrUpdate(it)
                editingEntry = null
            },
            onDelete = {
                viewModel.delete(it)
                editingEntry = null
            },
            onDismiss = { editingEntry = null }
        )
    }
}
