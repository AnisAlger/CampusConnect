package com.campusconnect.ui.components

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

@Composable
fun TimetableGrid(
    entries: List<TimetableEntry>,
    onCellClick: (Int, Int) -> Unit
) {
    val days = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
    val hours = (8..18).toList()

    Column {
        Row {
            Spacer(modifier = Modifier.width(48.dp))
            days.forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        hours.forEach { hour ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$hour h",
                    modifier = Modifier.width(48.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                for (day in 0..5) {
                    val entry = entries.find { it.dayOfWeek == day && it.startHour == hour }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(2.dp)
                            .background(if (entry != null) Color.Cyan else Color.LightGray)
                            .clickable { onCellClick(day, hour) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = entry?.label ?: "")
                    }
                }
            }
        }
    }
}
