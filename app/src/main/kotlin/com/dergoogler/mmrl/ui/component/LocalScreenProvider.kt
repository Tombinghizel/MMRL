package com.dergoogler.mmrl.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.dergoogler.mmrl.ui.providable.LocalHazeState
import dev.chrisbanes.haze.rememberHazeState

/**
 * Provides the screen with composition locals that shouldn't be used globally.
 */
@Composable
fun LocalScreenProvider(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalHazeState provides rememberHazeState()
    ) {
        content()
    }
}