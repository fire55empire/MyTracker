package com.mytracker.app.ui.activegoal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytracker.app.data.repository.GoalRepository
import com.mytracker.app.domain.model.Goal
import com.mytracker.app.domain.model.toDomain
import com.mytracker.app.utils.NotificationHelper
import com.mytracker.app.utils.TimeProvider
import com.mytracker.app.utils.WindowChecker
import com.mytracker.app.worker.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Active Goal screen.
 */
@HiltViewModel
class ActiveGoalViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val timeProvider: TimeProvider,
    private val windowChecker: WindowChecker,
    private val notificationHelper: NotificationHelper,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ActiveGoalUiState>(ActiveGoalUiState.Loading)
    val uiState: StateFlow<ActiveGoalUiState> = _uiState.asStateFlow()
    
    init {
        loadGoal()
    }
    
    private fun loadGoal() {
        viewModelScope.launch {
            repository.getActiveGoalFlow()
                .combine(flow { emit(Unit) }) { goal, _ -> goal }
                .collect { goalEntity ->
                    if (goalEntity == null) {
                        _uiState.value = ActiveGoalUiState.NoGoal
                    } else {
                        val goal = goalEntity.toDomain()
                        loadGoalData(goal)
                    }
                }
        }
    }
    
    private suspend fun loadGoalData(goal: Goal) {
        repository.getTotalPressCountFlow(goal.id)
            .collect { totalPresses ->
                val today = timeProvider.currentDate()
                val todayPresses = repository.getPressRecordsForDate(goal.id, today)
                
                val windowStates = goal.timeWindows.map { window ->
                    WindowState(
                        window = window,
                        isCompleted = todayPresses.any { it.windowIndex == window.index }
                    )
                }
                
                _uiState.value = ActiveGoalUiState.Success(
                    goal = goal,
                    totalPresses = totalPresses,
                    windowStates = windowStates,
                    message = null
                )
            }
    }
    
    fun onButtonPressed() {
        val state = _uiState.value
        if (state !is ActiveGoalUiState.Success) return
        
        val goal = state.goal
        val currentWindowIndex = windowChecker.getCurrentWindowIndex(goal.timeWindows)
        
        if (currentWindowIndex == null) {
            _uiState.value = state.copy(
                message = "You can only report inside a scheduled window."
            )
            return
        }
        
        viewModelScope.launch {
            val today = timeProvider.currentDate()
            val success = repository.recordPress(goal.id, currentWindowIndex, today)
            
            if (success) {
                // Send praise notification
                notificationHelper.sendPraiseNotification(goal.title)
                
                _uiState.value = state.copy(
                    message = "Progress recorded! 🎉"
                )
                
                // Reload to update UI
                loadGoalData(goal)
            } else {
                _uiState.value = state.copy(
                    message = "You've already pressed for this window today."
                )
            }
        }
    }
    
    fun onCancelGoal(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state !is ActiveGoalUiState.Success) return
        
        viewModelScope.launch {
            // Cancel all notifications
            notificationScheduler.cancelNotifications(state.goal.id)
            notificationHelper.cancelAllNotifications()
            
            // Delete goal and all records
            repository.deleteGoal(state.goal.id)
            
            onSuccess()
        }
    }
    
    fun clearMessage() {
        val state = _uiState.value
        if (state is ActiveGoalUiState.Success) {
            _uiState.value = state.copy(message = null)
        }
    }
}

/**
 * UI state for Active Goal screen.
 */
sealed class ActiveGoalUiState {
    object Loading : ActiveGoalUiState()
    object NoGoal : ActiveGoalUiState()
    data class Success(
        val goal: Goal,
        val totalPresses: Int,
        val windowStates: List<WindowState>,
        val message: String?
    ) : ActiveGoalUiState()
}

data class WindowState(
    val window: com.mytracker.app.data.entity.TimeWindow,
    val isCompleted: Boolean
)

