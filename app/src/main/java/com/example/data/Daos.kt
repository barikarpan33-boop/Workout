package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NexusDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStat(): Flow<UserStat?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserStat(stat: UserStat)

    @Query("SELECT * FROM workout_logs ORDER BY dateMillis ASC")
    fun getWorkoutLogs(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs ORDER BY dateMillis DESC LIMIT 7")
    fun getRecentWorkoutLogs(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog)

    @Query("SELECT * FROM workout_presets")
    fun getPresets(): Flow<List<WorkoutPreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: WorkoutPreset)

    @Query("DELETE FROM workout_presets WHERE id = :presetId")
    suspend fun deletePreset(presetId: Int)
}
