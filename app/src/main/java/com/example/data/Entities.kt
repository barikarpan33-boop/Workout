package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStat(
    @PrimaryKey val id: Int = 1,
    val name: String = "Player",
    val level: Int = 1,
    val xp: Int = 0,
    val rank: String = "E-Rank",
    val totalCalories: Int = 0,
    val totalWorkouts: Int = 0
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val presetName: String,
    val skipRatePercentage: Int,
    val caloriesBurned: Int,
    val xpGained: Int
)

@Entity(tableName = "workout_presets")
data class WorkoutPreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val exercisesJson: String, // Serialized list of exercises like "10x Pushups,15x Situps"
    val difficulty: String = "E-Rank"
)
