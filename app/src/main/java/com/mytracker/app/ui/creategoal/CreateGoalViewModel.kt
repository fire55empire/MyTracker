package com.mytracker.app.ui.creategoal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytracker.app.data.entity.TimeWindow
import com.mytracker.app.data.repository.GoalRepository
import com.mytracker.app.domain.validation.GoalValidator
import com.mytracker.app.domain.validation.ValidationResult
import com.mytracker.app.worker.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Create Goal screen.
 */
@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreateGoalUiState())
    val uiState: StateFlow<CreateGoalUiState> = _uiState.asStateFlow()
    
    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title, errorMessage = null)
    }
    
    fun onDurationDaysChanged(days: String) {
        val daysInt = days.toIntOrNull() ?: 0
        _uiState.value = _uiState.value.copy(durationDays = days, errorMessage = null)
    }
    
    fun addTimeWindow(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        val currentWindows = _uiState.value.timeWindows.toMutableList()
        val newWindow = TimeWindow(
            index = currentWindows.size,
            startHour = startHour,
            startMinute = startMinute,
            endHour = endHour,
            endMinute = endMinute
        )
        
        // Validate the new window
        if (!newWindow.isValid()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Invalid time window. End time must be after start time."
            )
            return
        }
        
        // Check for overlaps
        currentWindows.forEach { existing ->
            if (existing.overlaps(newWindow)) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Time window overlaps with existing window: ${existing.toDisplayString()}"
                )
                return
            }
        }
        
        currentWindows.add(newWindow)
        _uiState.value = _uiState.value.copy(timeWindows = currentWindows, errorMessage = null)
    }
    
    fun removeTimeWindow(index: Int) {
        val currentWindows = _uiState.value.timeWindows.toMutableList()
        currentWindows.removeAt(index)
        // Re-index windows
        val reindexed = currentWindows.mapIndexed { idx, window ->
            window.copy(index = idx)
        }
        _uiState.value = _uiState.value.copy(timeWindows = reindexed)
    }
    
    fun createGoal(onSuccess: () -> Unit) {
        val state = _uiState.value
        val days = state.durationDays.toIntOrNull() ?: 0
        
        // Validate
        val validationResult = GoalValidator.validateGoal(
            state.title,
            days,
            state.timeWindows
        )
        
        if (validationResult is ValidationResult.Error) {
            _uiState.value = state.copy(errorMessage = validationResult.message)
            return
        }
        
        // Create goal
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            try {
                val goalId = repository.createGoal(
                    title = state.title,
                    durationDays = days,
                    timeWindows = state.timeWindows
                )
                
                // Schedule notifications
                notificationScheduler.scheduleNotifications(goalId)
                
                _uiState.value = state.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    errorMessage = "Failed to create goal: ${e.message}"
                )
            }
        }
    }
}

/**
 * UI state for Create Goal screen.
 */
data class CreateGoalUiState(
    val title: String = "",
    val durationDays: String = "",
    val timeWindows: List<TimeWindow> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

