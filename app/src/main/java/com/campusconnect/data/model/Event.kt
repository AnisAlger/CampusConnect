package com.campusconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subjectId: Int,
    val startTime: Long,
    val endTime: Long,
    val location: String? = null,
    val reminderEnabled: Boolean = true
)

