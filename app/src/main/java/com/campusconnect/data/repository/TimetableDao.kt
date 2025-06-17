package com.campusconnect.data.repository

import androidx.room.*
import com.campusconnect.data.model.TimetableEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable_entries")
    fun getAll(): Flow<List<TimetableEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TimetableEntry)

    @Delete
    suspend fun delete(entry: TimetableEntry)
}
