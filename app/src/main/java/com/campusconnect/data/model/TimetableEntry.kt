package com.campusconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_entries")
data class TimetableEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int,
    val hour: Int,
    val label: String,
    val colorHex: String = "#D0F0C0" // Couleur par défaut
)
