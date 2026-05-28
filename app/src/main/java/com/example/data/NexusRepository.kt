package com.example.data

import kotlinx.coroutines.flow.Flow

class NexusRepository(private val dao: NexusDao) {
    val userStat: Flow<UserStat?> = dao.getUserStat()
    val allLogs: Flow<List<WorkoutLog>> = dao.getWorkoutLogs()
    val recentLogs: Flow<List<WorkoutLog>> = dao.getRecentWorkoutLogs()
    val allPresets: Flow<List<WorkoutPreset>> = dao.getPresets()

    suspend fun saveUserStat(stat: UserStat) = dao.saveUserStat(stat)

    suspend fun insertWorkoutLog(log: WorkoutLog) = dao.insertWorkoutLog(log)

    suspend fun insertPreset(preset: WorkoutPreset) = dao.insertPreset(preset)
    
    suspend fun deletePreset(presetId: Int) = dao.deletePreset(presetId)
}
