package com.mytracker.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mytracker.app.data.entity.TimeWindow
import com.mytracker.app.data.repository.GoalRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumentation test for the active goal screen.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ActiveGoalScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Inject
    lateinit var repository: GoalRepository
    
    @Before
    fun setup() {
        hiltRule.inject()
        
        // Create a test goal
        runBlocking {
            repository.createGoal(
                title = "Test Goal",
                durationDays = 7,
                timeWindows = listOf(
                    TimeWindow(0, 0, 0, 23, 59) // All day window for testing
                )
            )
        }
    }
    
    @Test
    fun activeGoalScreen_displaysGoalTitle() {
        composeTestRule.waitForIdle()
        
        // Verify the goal title is displayed
        composeTestRule.onNodeWithText("Test Goal")
            .assertExists()
    }
    
    @Test
    fun activeGoalScreen_displaysProgressButton() {
        composeTestRule.waitForIdle()
        
        // Verify the record progress button exists
        composeTestRule.onNodeWithText("RECORD PROGRESS")
            .assertExists()
    }
    
    @Test
    fun activeGoalScreen_cancelGoal_showsConfirmationDialog() {
        composeTestRule.waitForIdle()
        
        // Click the cancel goal button
        composeTestRule.onNodeWithText("Cancel Goal")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Verify confirmation dialog is shown
        composeTestRule.onNodeWithText("Cancel Goal?")
            .assertExists()
        
        composeTestRule.onNodeWithText("This will delete your goal and all progress. This action cannot be undone.")
            .assertExists()
    }
}

