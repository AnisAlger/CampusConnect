package com.campusconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.TimetableEntry

@Composable
fun TimetableDialog(
    entry: TimetableEntry,
    onSave: (TimetableEntry) -> Unit,
    onDelete: (TimetableEntry) -> Unit,
    onDismiss: () -> Unit
) {
    var label by remember { mutableStateOf(TextFieldValue(entry.label)) }
    var color by remember { mutableStateOf(Color(android.graphics.Color.parseColor(entry.colorHex))) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le cours") },
        text = {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Nom du cours") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ColorPicker(selectedColor = color, onColorSelected = { color = it })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(entry.copy(label = label.text, colorHex = "#%06X".format(0xFFFFFF and color.toArgb())))
            }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            Row {
                if (entry.label.isNotBlank()) {
                    OutlinedButton(onClick = { onDelete(entry) }) {
                        Text("Supprimer")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        }
    )
}
