package com.campusconnect.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.Event
import com.campusconnect.ui.components.AddEventDialog
import com.campusconnect.ui.components.EventItem
import com.campusconnect.viewmodel.EventViewModel

@Composable
fun EventsScreen(eventViewModel: EventViewModel) {
    val context = LocalContext.current
    val events by eventViewModel.events.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un événement")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Événements à venir", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(events) { event ->
                    EventItem(
                        event = event,
                        onClick = {},
                        onDelete = {
                            eventViewModel.deleteEvent(event)
                            Toast.makeText(context, "Événement supprimé", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showDialog) {
        AddEventDialog(
            onAdd = { title, location, start, end ->
                val newEvent = Event(
                    id = 0, // auto-généré par Room
                    title = title,
                    subjectId = 0,
                    startTime = start,
                    endTime = end,
                    location = location
                )
                eventViewModel.addEvent(newEvent)
                showDialog = false
                Toast.makeText(context, "Événement ajouté", Toast.LENGTH_SHORT).show()
            },
            onCancel = { showDialog = false }
        )
    }
}