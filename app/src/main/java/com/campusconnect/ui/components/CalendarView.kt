package com.campusconnect.ui.components

import android.widget.CalendarView
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import java.util.*

@Composable
fun CalendarViewComponent(
    selectedDate: Long,
    onDateChange: (Long) -> Unit
) {
    AndroidView(
        factory = { context ->
            CalendarView(context).apply {
                date = selectedDate
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    onDateChange(calendar.timeInMillis)
                }
            }
        },
        update = { it.date = selectedDate }
    )
}