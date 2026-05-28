package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NexusRepository
import com.example.data.UserStat
import com.example.data.WorkoutLog
import com.example.data.WorkoutPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NexusViewModel(private val repository: NexusRepository) : ViewModel() {

    val userStat: StateFlow<UserStat?> = repository.userStat.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val logs: StateFlow<List<WorkoutLog>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val presets: StateFlow<List<WorkoutPreset>> = repository.allPresets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            // Seed initial preset if empty
            repository.allPresets.collect { list ->
                if (list.isEmpty()) {
                    repository.insertPreset(
                        WorkoutPreset(
                            name = "Daily Quest: E-Rank",
                            exercisesJson = "100x Pushups|100x Situps|100x Squats|10km Run",
                            difficulty = "E-Rank"
                        )
                    )
                }
            }
        }
    }

    fun acceptSystemLogin(playerName: String) {
        viewModelScope.launch {
            repository.saveUserStat(UserStat(name = playerName))
        }
    }

    fun completeWorkout(presetId: Int, skipRatePercentage: Int, durationSecs: Int, presetName: String) {
        viewModelScope.launch {
            // Calculate offline AI calories: 
            // base burn (duration * 0.15) minus skip penalty
            val baseCalories = durationSecs * 0.15
            val penalty = baseCalories * (skipRatePercentage / 100f)
            val actualBurn = (baseCalories - penalty).toInt().coerceAtLeast(0)
            
            // Calculate XP 
            val xpGain = (actualBurn * 1.5).toInt()
            
            repository.insertWorkoutLog(
                WorkoutLog(
                    dateMillis = System.currentTimeMillis(),
                    presetName = presetName,
                    skipRatePercentage = skipRatePercentage,
                    caloriesBurned = actualBurn,
                    xpGained = xpGain
                )
            )
            
            val currentStat = userStat.value ?: return@launch
            val newXp = currentStat.xp + xpGain
            var newLevel = currentStat.level
            var currentXpForNextLevel = 100 * newLevel
            
            var remainingXp = newXp
            while (remainingXp >= currentXpForNextLevel) {
                remainingXp -= currentXpForNextLevel
                newLevel++
                currentXpForNextLevel = 100 * newLevel
            }
            
            val newRank = when {
                newLevel >= 50 -> "S-Rank"
                newLevel >= 40 -> "A-Rank"
                newLevel >= 30 -> "B-Rank"
                newLevel >= 20 -> "C-Rank"
                newLevel >= 10 -> "D-Rank"
                else -> "E-Rank"
            }

            repository.saveUserStat(
                currentStat.copy(
                    level = newLevel,
                    xp = remainingXp,
                    rank = newRank,
                    totalCalories = currentStat.totalCalories + actualBurn,
                    totalWorkouts = currentStat.totalWorkouts + 1
                )
            )
        }
    }

    fun addCustomPreset(name: String, exercisesString: String) {
        viewModelScope.launch {
            repository.insertPreset(
                WorkoutPreset(
                    name = name,
                    exercisesJson = exercisesString,
                    difficulty = "Custom"
                )
            )
        }
    }
}
