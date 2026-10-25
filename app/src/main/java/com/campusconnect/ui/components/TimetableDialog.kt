package com.campusconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campusconnect.data.model.TimetableEntry
import com.campusconnect.ui.theme.IndigoPrimaryLight

@Composable
fun TimetableDialog(
    entry: TimetableEntry,
    onSave: (TimetableEntry) -> Unit,
    onDelete: (TimetableEntry) -> Unit,
    onDismiss: () -> Unit
) {
    val dayNames = listOf("Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi")
    val dayName = dayNames.getOrElse(entry.dayOfWeek) { "Jour" }

    var label by remember { mutableStateOf(entry.label) }
    var selectedType by remember {
        mutableStateOf(if (entry.type.isBlank() || entry.type == "Cours/TD/TP") "Cours" else entry.type)
    }
    var color by remember {
        val initialColor = try {
            Color(android.graphics.Color.parseColor(entry.colorHex))
        } catch (_: Exception) {
            IndigoPrimaryLight
        }
        mutableStateOf(initialColor)
    }
    var isLabelError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = if (entry.label.isBlank()) "Ajouter un cours" else "Modifier le cours",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Time & Day Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "$dayName • ${entry.timeSlot}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Type Selector (Cours / TD / TP)
                Column {
                    Text(
                        text = "Type de séance",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Cours", "TD", "TP").forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Course Name
                OutlinedTextField(
                    value = label,
                    onValueChange = {
                        label = it
                        isLabelError = false
                    },
                    label = { Text("Matière / Intitulé du cours *") },
                    leadingIcon = {
                        Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    isError = isLabelError,
                    supportingText = {
                        if (isLabelError) Text("L'intitulé est obligatoire", color = MaterialTheme.colorScheme.error)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Color Picker
                Column {
                    Text(
                        text = "Couleur d'identification",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPicker(
                        selectedColor = color,
                        onColorSelected = { color = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (label.isBlank()) {
                        isLabelError = true
                        return@Button
                    }
                    onSave(
                        entry.copy(
                            label = label.trim(),
                            type = selectedType,
                            colorHex = "#%06X".format(0xFFFFFF and color.toArgb())
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            Row {
                if (entry.label.isNotBlank()) {
                    TextButton(
                        onClick = { onDelete(entry) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Supprimer")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        }
    )
}