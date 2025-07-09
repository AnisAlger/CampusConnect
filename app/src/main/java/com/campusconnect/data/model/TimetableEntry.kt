package com.campusconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_entries")
data class TimetableEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int,
    val startHour: Int, // e.g., 8, 9, 14
    val timeSlot: String, // ex: "08:00 - 09:30" (can still keep this for display)
    val label: String,
    val colorHex: String = "#D0F0C0",
    val type: String = "Cours/TD/TP"
)
