package com.campusconnect.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEventDialog(
    onAdd: (String, String?, Long, Long) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var startTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTimeMillis by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Ajouter un événement") },
        confirmButton = {
            Button(onClick = {
                onAdd(title, location, startTimeMillis, endTimeMillis)
            }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text("Annuler")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lieu (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Date et heure de début
                Text("Début : ${dateFormat.format(Date(startTimeMillis))}")
                Button(
                    onClick = {
                        pickDateTime(context) { selectedTime ->
                            startTimeMillis = selectedTime
                            if (endTimeMillis <= selectedTime) {
                                endTimeMillis = selectedTime + 3600000 // 1h après
                            }
                        }
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text("Choisir la date de début")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date et heure de fin
                Text("Fin : ${dateFormat.format(Date(endTimeMillis))}")
                Button(
                    onClick = {
                        pickDateTime(context) { selectedTime ->
                            endTimeMillis = selectedTime
                        }
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text("Choisir la date de fin")
                }
            }
        }
    )
}

private fun pickDateTime(context: Context, onDateTimeSelected: (Long) -> Unit) {
    val now = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val calendar = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute)
                    }
                    onDateTimeSelected(calendar.timeInMillis)
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            ).show()
        },
        now.get(Calendar.YEAR),
        now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH)
    ).show()
}
