package com.campusconnect.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.Event
import com.campusconnect.ui.components.AddEventDialog
import com.campusconnect.ui.components.EventItem
import com.campusconnect.viewmodel.EventViewModel

enum class EventFilter {
    ALL, UPCOMING, PAST
}

@Composable
fun EventsScreen(eventViewModel: EventViewModel) {
    val context = LocalContext.current
    val events by eventViewModel.events.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf(EventFilter.UPCOMING) }

    val now = System.currentTimeMillis()
    val upcomingCount = events.count { it.endTime >= now }
    val pastCount = events.count { it.endTime < now }

    val filteredEvents = remember(events, currentFilter, searchQuery) {
        events.filter { event ->
            val matchesFilter = when (currentFilter) {
                EventFilter.ALL -> true
                EventFilter.UPCOMING -> event.endTime >= now
                EventFilter.PAST -> event.endTime < now
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                event.title.contains(searchQuery, ignoreCase = true) ||
                        (event.location?.contains(searchQuery, ignoreCase = true) == true)
            }
            matchesFilter && matchesSearch
        }.sortedWith(
            if (currentFilter == EventFilter.PAST) compareByDescending { it.startTime }
            else compareBy { it.startTime }
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nouvel événement", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "Événements du campus",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (upcomingCount > 0) "$upcomingCount événements prévus à venir" else "Aucun événement à venir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher un événement, une salle...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = currentFilter == EventFilter.UPCOMING,
                    onClick = { currentFilter = EventFilter.UPCOMING },
                    label = { Text("À venir ($upcomingCount)") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = currentFilter == EventFilter.ALL,
                    onClick = { currentFilter = EventFilter.ALL },
                    label = { Text("Tous (${events.size})") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = currentFilter == EventFilter.PAST,
                    onClick = { currentFilter = EventFilter.PAST },
                    label = { Text("Passés ($pastCount)") },
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Events List
            if (filteredEvents.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredEvents, key = { it.id }) { event ->
                        EventItem(
                            event = event,
                            onClick = {},
                            onDelete = {
                                eventViewModel.deleteEvent(event)
                                Toast.makeText(context, "Événement supprimé", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty()) "Aucun événement correspondant"
                            else if (currentFilter == EventFilter.UPCOMING) "Aucun événement à venir"
                            else "Aucun événement enregistré",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty()) "Vérifiez vos critères de recherche."
                            else "Planifiez vos examens, conférences ou rassemblements associatifs !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { showDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ajouter un événement")
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddEventDialog(
            onAdd = { title, location, start, end ->
                val newEvent = Event(
                    id = 0,
                    title = title,
                    subjectId = 0,
                    startTime = start,
                    endTime = end,
                    location = location
                )
                eventViewModel.addEvent(newEvent)
                showDialog = false
                Toast.makeText(context, "Événement créé avec succès", Toast.LENGTH_SHORT).show()
            },
            onCancel = { showDialog = false }
        )
    }
}