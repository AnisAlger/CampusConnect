package com.campusconnect.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.TimetableEntry
import com.campusconnect.ui.components.TimetableDialog
import com.campusconnect.viewmodel.TimetableViewModel

@Composable
fun CalendarScreen(viewModel: TimetableViewModel) {
    val entries by viewModel.entries.collectAsState()

    val days = listOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")

    val timeSlots = listOf(
        8 to "08:00 - 09:30",
        9 to "09:30 - 11:00",
        11 to "11:00 - 12:30",
        13 to "13:00 - 14:30",
        14 to "14:30 - 16:00",
        16 to "16:00 - 17:30"
    )

    var editingEntry by remember { mutableStateOf<TimetableEntry?>(null) }

    val scrollStateH = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val totalHeight = maxHeight
        val rowHeight = totalHeight / (timeSlots.size + 1) // +1 for header

        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollStateH)
                .padding(8.dp)
        ) {
            // Header row
            Row(modifier = Modifier.height(rowHeight)) {
                Spacer(modifier = Modifier.width(80.dp))
                days.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .width(140.dp)
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Timetable rows
            timeSlots.forEach { (startHour, slotText) ->
                Row(modifier = Modifier.height(rowHeight)) {
                    // Hour label
                    Text(
                        text = slotText,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )

                    days.forEachIndexed { dayIndex, _ ->
                        val entry = entries.find { it.dayOfWeek == dayIndex && it.startHour == startHour }

                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .padding(2.dp)
                                .border(0.5.dp, Color.Gray)
                                .background(Color(android.graphics.Color.parseColor(entry?.colorHex ?: "#FFFFFF")))
                                .clickable {
                                    editingEntry = entry ?: TimetableEntry(
                                        dayOfWeek = dayIndex,
                                        startHour = startHour,
                                        timeSlot = slotText,
                                        label = ""
                                    )
                                }
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = entry?.label ?: "",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp),
                                softWrap = true,
                                overflow = TextOverflow.Clip,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    // Editing dialog
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