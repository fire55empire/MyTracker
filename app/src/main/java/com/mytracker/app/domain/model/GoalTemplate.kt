package com.mytracker.app.domain.model

import com.mytracker.app.data.entity.TimeWindow

/**
 * Шаблон цели с предустановленными временными окнами
 */
data class GoalTemplate(
    val id: String,
    val name: String,
    val description: String,
    val icon: String, // emoji
    val category: TemplateCategory,
    val durationDays: Int,
    val timeWindows: List<TimeWindow>
)

enum class TemplateCategory {
    HEALTH,      // Здоровье
    PRODUCTIVITY, // Продуктивность
    LEARNING,    // Обучение
    SPORT,       // Спорт
    HABITS       // Привычки
}

/**
 * Предустановленные шаблоны целей
 */
object GoalTemplates {
    
    val templates = listOf(
        // Здоровье
        GoalTemplate(
            id = "water_8_glasses",
            name = "Пить воду 8 раз в день",
            description = "Здоровая привычка пить воду в течение дня",
            icon = "💧",
            category = TemplateCategory.HEALTH,
            durationDays = 30,
            timeWindows = listOf(
                TimeWindow(0, 8, 0, 20, 0)  // 8:00-20:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "morning_exercise",
            name = "Утренняя зарядка",
            description = "Начни день с энергией! Зарядка каждое утро",
            icon = "🏃",
            category = TemplateCategory.SPORT,
            durationDays = 30,
            timeWindows = listOf(
                TimeWindow(0, 7, 0, 9, 0)  // 7:00-9:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "meditation",
            name = "Медитация",
            description = "10 минут спокойствия для ясности ума дважды в день",
            icon = "🧘",
            category = TemplateCategory.HEALTH,
            durationDays = 21,
            timeWindows = listOf(
                TimeWindow(0, 7, 0, 9, 0),   // Утро 7:00-9:00
                TimeWindow(1, 20, 0, 22, 0)  // Вечер 20:00-22:00
            )
        ),
        
        GoalTemplate(
            id = "reading",
            name = "Чтение книг",
            description = "30 минут чтения каждый вечер",
            icon = "📚",
            category = TemplateCategory.LEARNING,
            durationDays = 30,
            timeWindows = listOf(
                TimeWindow(0, 19, 0, 23, 0)  // 19:00-23:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "language_practice",
            name = "Изучение английского",
            description = "15 минут практики каждый день",
            icon = "🗣️",
            category = TemplateCategory.LEARNING,
            durationDays = 30,
            timeWindows = listOf(
                TimeWindow(0, 17, 0, 21, 0)  // 17:00-21:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "work_focus",
            name = "Глубокая работа",
            description = "2 часа концентрированной работы без отвлечений",
            icon = "💼",
            category = TemplateCategory.PRODUCTIVITY,
            durationDays = 14,
            timeWindows = listOf(
                TimeWindow(0, 9, 0, 12, 0)  // 9:00-12:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "no_phone_morning",
            name = "Без телефона по утрам",
            description = "Первый час после пробуждения без смартфона",
            icon = "📵",
            category = TemplateCategory.HABITS,
            durationDays = 21,
            timeWindows = listOf(
                TimeWindow(0, 7, 0, 9, 0)  // 7:00-9:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "gratitude_journal",
            name = "Дневник благодарности",
            description = "Записывай 3 вещи, за которые благодарен",
            icon = "✨",
            category = TemplateCategory.HABITS,
            durationDays = 30,
            timeWindows = listOf(
                TimeWindow(0, 20, 0, 23, 0)  // 20:00-23:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "gym_workout",
            name = "Тренировка в зале",
            description = "1 час тренировки ежедневно",
            icon = "💪",
            category = TemplateCategory.SPORT,
            durationDays = 30,
            timeWindows = listOf(
                TimeWindow(0, 18, 0, 21, 0)  // 18:00-21:00 ежедневно
            )
        ),
        
        GoalTemplate(
            id = "healthy_breakfast",
            name = "Полезный завтрак",
            description = "Начни день с правильного питания",
            icon = "🥗",
            category = TemplateCategory.HEALTH,
            durationDays = 30,
            timeWindows = listOf(
                TimeWindow(0, 7, 0, 10, 0)  // 7:00-10:00 ежедневно
            )
        ),
    )
    
    /**
     * Получить шаблоны по категории
     */
    fun getByCategory(category: TemplateCategory): List<GoalTemplate> {
        return templates.filter { it.category == category }
    }
    
    /**
     * Получить шаблон по ID
     */
    fun getById(id: String): GoalTemplate? {
        return templates.find { it.id == id }
    }
    
    /**
     * Получить название категории на русском
     */
    fun getCategoryName(category: TemplateCategory): String {
        return when (category) {
            TemplateCategory.HEALTH -> "Здоровье"
            TemplateCategory.PRODUCTIVITY -> "Продуктивность"
            TemplateCategory.LEARNING -> "Обучение"
            TemplateCategory.SPORT -> "Спорт"
            TemplateCategory.HABITS -> "Привычки"
        }
    }
}

