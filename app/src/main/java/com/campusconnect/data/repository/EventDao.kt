package com.campusconnect.data.repository

import androidx.room.*
import com.campusconnect.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM events WHERE startTime >= :currentTime ORDER BY startTime ASC LIMIT 5")
    fun getUpcomingEvents(currentTime: Long): Flow<List<Event>>

}
