package com.campusconnect.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusconnect.data.model.TimetableEntry
import com.campusconnect.ui.components.TimetableDialog
import com.campusconnect.ui.theme.IndigoPrimaryLight
import com.campusconnect.viewmodel.TimetableViewModel
import java.util.*

enum class TimetableViewMode {
    DAY_AGENDA, WEEK_GRID
}

@Composable
fun CalendarScreen(viewModel: TimetableViewModel) {
    val entries by viewModel.entries.collectAsState()

    val daysFull = listOf("Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi")
    val daysShort = listOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")

    val timeSlots = listOf(
        8 to "08:00 - 09:30",
        9 to "09:30 - 11:00",
        11 to "11:00 - 12:30",
        13 to "13:00 - 14:30",
        14 to "14:30 - 16:00",
        16 to "16:00 - 17:30"
    )

    // Current day initialization
    val todayIndex = remember {
        val cal = Calendar.getInstance()
        cal.get(Calendar.DAY_OF_WEEK) - 1
    }

    var selectedDayIndex by remember { mutableStateOf(todayIndex) }
    var viewMode by remember { mutableStateOf(TimetableViewMode.DAY_AGENDA) }
    var editingEntry by remember { mutableStateOf<TimetableEntry?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Emploi du temps",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Gérez vos cours, TD et TP de la semaine",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // View Toggle (Day Agenda vs Week Grid)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(2.dp)
                ) {
                    IconButton(
                        onClick = { viewMode = TimetableViewMode.DAY_AGENDA },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (viewMode == TimetableViewMode.DAY_AGENDA) MaterialTheme.colorScheme.surface
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewAgenda,
                            contentDescription = "Vue Jour",
                            tint = if (viewMode == TimetableViewMode.DAY_AGENDA) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewMode = TimetableViewMode.WEEK_GRID },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (viewMode == TimetableViewMode.WEEK_GRID) MaterialTheme.colorScheme.surface
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarViewWeek,
                            contentDescription = "Vue Semaine",
                            tint = if (viewMode == TimetableViewMode.WEEK_GRID) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day Selector Row (for Day Agenda mode)
            if (viewMode == TimetableViewMode.DAY_AGENDA) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    daysShort.forEachIndexed { index, shortName ->
                        val isSelected = selectedDayIndex == index
                        val isToday = index == todayIndex
                        val dayCourseCount = entries.count { it.dayOfWeek == index && it.label.isNotBlank() }

                        Card(
                            onClick = { selectedDayIndex = index },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = if (!isSelected && isToday) {
                                BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                            } else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
                            modifier = Modifier.width(68.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = shortName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isToday) "Aujourd'hui" else if (dayCourseCount > 0) "$dayCourseCount cours" else "Libre",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                    else if (isToday) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Content Area (Day Agenda or Week Grid)
        if (viewMode == TimetableViewMode.DAY_AGENDA) {
            // Day Agenda View
            val dayEntries = timeSlots.map { (startHour, slotText) ->
                val entry = entries.find { it.dayOfWeek == selectedDayIndex && it.startHour == startHour }
                Triple(startHour, slotText, entry)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(dayEntries) { (startHour, slotText, entry) ->
                    if (entry != null && entry.label.isNotBlank()) {
                        // Booked course card
                        val courseColor = remember(entry.colorHex) {
                            try {
                                Color(android.graphics.Color.parseColor(entry.colorHex))
                            } catch (_: Exception) {
                                IndigoPrimaryLight
                            }
                        }

                        Card(
                            onClick = { editingEntry = entry },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left colored indicator
                                Box(
                                    modifier = Modifier
                                        .size(width = 5.dp, height = 48.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(courseColor)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = courseColor.copy(alpha = 0.16f)
                                        ) {
                                            Text(
                                                text = entry.type.uppercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                color = courseColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = slotText,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = entry.label,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(onClick = { editingEntry = entry }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifier",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty slot card
                        Card(
                            onClick = {
                                editingEntry = TimetableEntry(
                                    dayOfWeek = selectedDayIndex,
                                    startHour = startHour,
                                    timeSlot = slotText,
                                    label = ""
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = slotText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+ Ajouter un cours",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Week Grid View
            val scrollStateH = rememberScrollState()
            val scrollStateV = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollStateV)
                        .horizontalScroll(scrollStateH)
                        .padding(8.dp)
                ) {
                    // Header row (Days)
                    Row(
                        modifier = Modifier
                            .height(44.dp)
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(84.dp))
                        daysShort.forEachIndexed { dayIndex, day ->
                            val isToday = dayIndex == todayIndex
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isToday) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier
                                    .width(130.dp)
                                    .padding(horizontal = 2.dp)
                            ) {
                                Text(
                                    text = day,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Rows for each time slot
                    timeSlots.forEach { (startHour, slotText) ->
                        Row(
                            modifier = Modifier
                                .height(76.dp)
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Hour label column
                            Text(
                                text = slotText,
                                modifier = Modifier
                                    .width(84.dp)
                                    .padding(end = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.End
                            )

                            // Day cells
                            daysShort.forEachIndexed { dayIndex, _ ->
                                val entry = entries.find { it.dayOfWeek == dayIndex && it.startHour == startHour }

                                val cellColor = remember(entry?.colorHex) {
                                    if (entry != null && entry.label.isNotBlank()) {
                                        try {
                                            Color(android.graphics.Color.parseColor(entry.colorHex))
                                        } catch (_: Exception) {
                                            IndigoPrimaryLight
                                        }
                                    } else {
                                        Color.Transparent
                                    }
                                }

                                Card(
                                    onClick = {
                                        editingEntry = entry ?: TimetableEntry(
                                            dayOfWeek = dayIndex,
                                            startHour = startHour,
                                            timeSlot = slotText,
                                            label = ""
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (entry != null && entry.label.isNotBlank()) {
                                            cellColor.copy(alpha = 0.22f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        }
                                    ),
                                    border = BorderStroke(
                                        0.8.dp,
                                        if (entry != null && entry.label.isNotBlank()) cellColor.copy(alpha = 0.6f)
                                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .width(130.dp)
                                        .fillMaxHeight()
                                        .padding(horizontal = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (entry != null && entry.label.isNotBlank()) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = entry.type.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = cellColor
                                                )
                                                Text(
                                                    text = entry.label,
                                                    textAlign = TextAlign.Center,
                                                    softWrap = true,
                                                    overflow = TextOverflow.Ellipsis,
                                                    maxLines = 2,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 11.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = "+",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Editing Dialog
    editingEntry?.let { entry ->
        TimetableDialog(
            entry = entry,
            onSave = { updatedEntry ->
                viewModel.addOrUpdate(updatedEntry)
                editingEntry = null
            },
            onDelete = { entryToDelete ->
                viewModel.delete(entryToDelete)
                editingEntry = null
            },
            onDismiss = { editingEntry = null }
        )
    }
}