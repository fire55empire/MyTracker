package com.mytracker.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test for the create goal flow.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CreateGoalFlowTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun createGoal_navigatesToActiveGoalScreen() {
        // Wait for the create goal screen to appear
        composeTestRule.waitForIdle()
        
        // Fill in the goal title
        composeTestRule.onNodeWithText("Goal Title")
            .performTextInput("Exercise Daily")
        
        // Fill in duration days
        composeTestRule.onNodeWithText("Number of Days")
            .performTextInput("7")
        
        // Add a time window
        composeTestRule.onNodeWithText("Add Time Window")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Set start time (default values should work)
        composeTestRule.onAllNodesWithText("Add")[0]
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Create the goal
        composeTestRule.onNodeWithText("Create Goal")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Verify we're on the active goal screen by checking for the "RECORD PROGRESS" button
        composeTestRule.onNodeWithText("RECORD PROGRESS")
            .assertExists()
    }
    
    @Test
    fun createGoal_withoutTimeWindow_showsError() {
        composeTestRule.waitForIdle()
        
        // Fill in the goal title
        composeTestRule.onNodeWithText("Goal Title")
            .performTextInput("Exercise Daily")
        
        // Fill in duration days
        composeTestRule.onNodeWithText("Number of Days")
            .performTextInput("7")
        
        // Try to create the goal without adding a time window
        composeTestRule.onNodeWithText("Create Goal")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Verify error message is shown
        composeTestRule.onNodeWithText("At least one time window is required")
            .assertExists()
    }
}

