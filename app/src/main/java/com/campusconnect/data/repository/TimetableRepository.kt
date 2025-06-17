package com.campusconnect.data.repository

import com.campusconnect.data.model.TimetableEntry
import kotlinx.coroutines.flow.Flow

class TimetableRepository(private val timetableDao: TimetableDao) {
    fun getAll(): Flow<List<TimetableEntry>> = timetableDao.getAll()
    suspend fun insert(entry: TimetableEntry) = timetableDao.insert(entry)
    suspend fun delete(entry: TimetableEntry) = timetableDao.delete(entry)
}
