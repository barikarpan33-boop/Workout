package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserStat::class, WorkoutLog::class, WorkoutPreset::class],
    version = 1,
    exportSchema = false
)
abstract class NexusDatabase : RoomDatabase() {
    abstract fun nexusDao(): NexusDao
}
