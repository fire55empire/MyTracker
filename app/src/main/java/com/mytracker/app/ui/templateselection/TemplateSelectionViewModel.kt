package com.mytracker.app.ui.templateselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytracker.app.data.repository.GoalRepository
import com.mytracker.app.domain.model.GoalTemplate
import com.mytracker.app.domain.model.GoalTemplates
import com.mytracker.app.domain.model.TemplateCategory
import com.mytracker.app.worker.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateSelectionViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(TemplateSelectionUiState())
    val uiState: StateFlow<TemplateSelectionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TemplateSelectionEvent>()
    val events: SharedFlow<TemplateSelectionEvent> = _events.asSharedFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        _uiState.update {
            it.copy(templates = GoalTemplates.templates)
        }
    }

    fun onCategorySelected(category: TemplateCategory?) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                filteredTemplates = if (category == null) {
                    GoalTemplates.templates
                } else {
                    GoalTemplates.getByCategory(category)
                }
            )
        }
    }

    fun onTemplateSelected(template: GoalTemplate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Создаем цель из шаблона
                val goalId = repository.createGoal(
                    title = template.name,
                    durationDays = template.durationDays,
                    timeWindows = template.timeWindows
                )
                
                // Планируем уведомления
                notificationScheduler.scheduleNotifications(goalId)
                
                // Отправляем событие успеха
                _events.emit(TemplateSelectionEvent.GoalCreated(goalId))
                
            } catch (e: Exception) {
                _events.emit(
                    TemplateSelectionEvent.ShowError("Ошибка создания цели: ${e.message}")
                )
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onCreateCustomGoal() {
        viewModelScope.launch {
            _events.emit(TemplateSelectionEvent.NavigateToCustomGoal)
        }
    }
}

data class TemplateSelectionUiState(
    val templates: List<GoalTemplate> = emptyList(),
    val filteredTemplates: List<GoalTemplate> = emptyList(),
    val selectedCategory: TemplateCategory? = null,
    val isLoading: Boolean = false
)

sealed class TemplateSelectionEvent {
    data class GoalCreated(val goalId: Long) : TemplateSelectionEvent()
    data class ShowError(val message: String) : TemplateSelectionEvent()
    object NavigateToCustomGoal : TemplateSelectionEvent()
}

