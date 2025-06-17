package com.campusconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campusconnect.data.model.Event
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventItem(
    event: Event,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // Sans remember

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, style = MaterialTheme.typography.titleMedium)

                if (!event.location.isNullOrEmpty()) {
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "Début : ${formatter.format(Date(event.startTime))}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Fin   : ${formatter.format(Date(event.endTime))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
