package com.dergoogler.mmrl.ui.component.toolbar

import android.R.attr.navigationIcon
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.dergoogler.mmrl.ui.providable.LocalHazeState
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@Composable
fun TopAppBarDefaults.color(enabled: Boolean, alpha: Float): TopAppBarColors {
    return if (enabled) {
        topAppBarColors().copy(
            scrolledContainerColor = Color.Transparent,
            containerColor = Color.Transparent
        )
    } else {
        val backgroundColor = MaterialTheme.colorScheme.background
            .copy(alpha = alpha)

        topAppBarColors().copy(
            scrolledContainerColor = backgroundColor,
            containerColor = backgroundColor
        )
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BlurToolbar(
    title: @Composable (Float) -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: @Composable (Boolean, Float) -> TopAppBarColors = { enabled, alpha ->
        TopAppBarDefaults.color(
            enabled,
            alpha
        )
    },
    noFade: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val prefs = LocalUserPreferences.current
    val state = scrollBehavior?.state
    var alpha by remember { mutableFloatStateOf(if (noFade) 1f else 0f) }
    val fadeDistance = 200f

    if (!noFade && state != null) {
        LaunchedEffect(state) {
            snapshotFlow { state.contentOffset }
                .collect { offset ->
                    val newAlpha = ((-offset) / fadeDistance).coerceIn(0f, 1f)
                    if ((newAlpha < 1f && newAlpha > 0f) || newAlpha != alpha) {
                        alpha = newAlpha
                    }
                }
        }
    } else if (noFade) {
        alpha = 1f // force fully opaque
    }

    val borderColor = MaterialTheme.colorScheme.outline

    val blur = if (prefs.enableBlur) {
        Modifier.hazeEffect(
            state = LocalHazeState.current,
            style = HazeMaterials.ultraThin()
        ) {
            this@hazeEffect.alpha = alpha
        }
    } else Modifier

    TopAppBar(
        title = { title(alpha) },
        modifier = Modifier
            .drawBehind {
                val borderSize = Dp.Hairline
                val y = size.height - borderSize.value
                drawLine(
                    color = borderColor.copy(alpha = alpha),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = borderSize.value
                )
            }
            .then(blur)
            .then(modifier),
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors(prefs.enableBlur, alpha),
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets,
        expandedHeight = expandedHeight,
    )
}
