package com.campusconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.TimetableEntry

@Composable
fun TimetableDialog(
    item: TimetableEntry,
    onSave: (TimetableEntry) -> Unit,
    onDelete: (TimetableEntry) -> Unit,
    onDismiss: () -> Unit
) {
    var label by remember { mutableStateOf(item.label) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                Button(onClick = {
                    onSave(item.copy(label = label))
                }) {
                    Text("Enregistrer")
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (item.label.isNotBlank()) {
                    OutlinedButton(onClick = { onDelete(item) }) {
                        Text("Supprimer")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = { Text("Modifier le cours") },
        text = {
            Column {
                Text("Jour: ${item.dayOfWeek} - Heure: ${item.hour}h")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Nom du cours") },
                    singleLine = true
                )
            }
        }
    )
}
