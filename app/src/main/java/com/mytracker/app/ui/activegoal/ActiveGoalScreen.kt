package com.mytracker.app.ui.activegoal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveGoalScreen(
    onNoGoal: () -> Unit,
    viewModel: ActiveGoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showCancelDialog by remember { mutableStateOf(false) }
    
    when (val state = uiState) {
        is ActiveGoalUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is ActiveGoalUiState.NoGoal -> {
            LaunchedEffect(Unit) {
                onNoGoal()
            }
        }
        
        is ActiveGoalUiState.Success -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(state.goal.title) }
                    )
                },
                snackbarHost = {
                    state.message?.let { message ->
                        Snackbar(
                            modifier = Modifier.padding(16.dp),
                            action = {
                                TextButton(onClick = { viewModel.clearMessage() }) {
                                    Text("Dismiss")
                                }
                            }
                        ) {
                            Text(message)
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Progress section
                    Text(
                        text = state.goal.calculateProgressPercentage(state.totalPresses),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    LinearProgressIndicator(
                        progress = state.goal.calculateProgress(state.totalPresses),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                    )
                    
                    Text(
                        text = "${state.totalPresses} / ${state.goal.totalRequiredPresses} presses",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Today's windows
                    Text(
                        "Today's Windows",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.windowStates) { windowState ->
                            WindowChip(windowState)
                        }
                    }
                    
                    Spacer(Modifier.weight(1f))
                    
                    // Main action button
                    Button(
                        onClick = { viewModel.onButtonPressed() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "RECORD PROGRESS",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    
                    // Cancel button
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel Goal")
                    }
                }
            }
            
            if (showCancelDialog) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    title = { Text("Cancel Goal?") },
                    text = { 
                        Text("This will delete your goal and all progress. This action cannot be undone.") 
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.onCancelGoal(onNoGoal)
                                showCancelDialog = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelDialog = false }) {
                            Text("Keep Goal")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WindowChip(windowState: WindowState) {
    FilterChip(
        selected = windowState.isCompleted,
        onClick = { },
        label = { 
            Text(
                windowState.window.toDisplayString(),
                style = MaterialTheme.typography.bodySmall
            ) 
        },
        leadingIcon = if (windowState.isCompleted) {
            { Icon(Icons.Default.Check, contentDescription = "Completed") }
        } else null
    )
}

