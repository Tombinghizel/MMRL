package com.dergoogler.mmrl.ui.component.toolbar

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.dergoogler.mmrl.ext.takeTrue
import com.dergoogler.mmrl.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlurNavigateUpToolbar(
    title: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: @Composable (Boolean, Float) -> TopAppBarColors = { enabled, alpha ->
        TopAppBarDefaults.color(
            enabled,
            alpha
        )
    },
    scrollBehavior: TopAppBarScrollBehavior? = null,
    noFade: Boolean = false,
) = BlurNavigateUpToolbar(
    modifier = modifier,
    title = title,
    subtitle = subtitle,
    onBack = { navController.popBackStack() },
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    enable = enable,
    noFade = noFade
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlurNavigateUpToolbar(
    title: String,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    subtitle: String? = null,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: @Composable (Boolean, Float) -> TopAppBarColors = { enabled, alpha ->
        TopAppBarDefaults.color(
            enabled,
            alpha
        )
    },
    scrollBehavior: TopAppBarScrollBehavior? = null,
    noFade: Boolean = false,
) = BlurNavigateUpToolbar(
    modifier = modifier,
    title = title,
    subtitle = subtitle,
    onBack = { (context as ComponentActivity).finish() },
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    enable = enable,
    noFade = noFade
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlurNavigateUpToolbar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: @Composable (Boolean, Float) -> TopAppBarColors = { enabled, alpha ->
        TopAppBarDefaults.color(
            enabled,
            alpha
        )
    },
    scrollBehavior: TopAppBarScrollBehavior? = null,
    noFade: Boolean = false,
) = BlurNavigateUpToolbar(
    modifier = modifier,
    title = {
        ToolbarTitle(title = title, subtitle = subtitle)
    },
    onBack = onBack,
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    enable = enable,
    noFade = noFade
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlurNavigateUpToolbar(
    title: @Composable (Float) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: @Composable (Boolean, Float) -> TopAppBarColors = { enabled, alpha ->
        TopAppBarDefaults.color(
            enabled,
            alpha
        )
    },
    scrollBehavior: TopAppBarScrollBehavior? = null,
    noFade: Boolean = false,
) = BlurToolbar(
    title = title,
    modifier = modifier,
    navigationIcon = {
        enable.takeTrue {
            IconButton(
                onClick = { if (it) onBack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = null
                )
            }
        }
    },
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    noFade = noFade
)