package com.campusconnect.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.campusconnect.data.model.*

@Database(
    entities = [Task::class, Event::class, TimetableEntry::class], // Ajout de TimetableEntry
    version = 2,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun timetableDao(): TimetableDao // Ajout du DAO de l’emploi du temps

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getDatabase(context: Context): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campus_database"
                ).fallbackToDestructiveMigration() // utile pour éviter les crashs si tu modifies les entités
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
