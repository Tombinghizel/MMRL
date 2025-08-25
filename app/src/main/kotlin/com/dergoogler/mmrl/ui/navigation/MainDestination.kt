package com.dergoogler.mmrl.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dergoogler.mmrl.R
import com.ramcosta.composedestinations.annotation.NavHostGraph
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ModulesScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RepositoriesScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@NavHostGraph
annotation class MainGraph

enum class MainDestination(
    val direction: DirectionDestinationSpec,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconFilled: Int,
    val requiresRoot: Boolean,
) {
    Home(
        direction = HomeScreenDestination,
        label = R.string.page_home,
        icon = R.drawable.home,
        iconFilled = R.drawable.home_filled,
        requiresRoot = false
    ),
    Repository(
        direction = RepositoriesScreenDestination,
        label = R.string.page_repositorys,
        icon = R.drawable.cloud,
        iconFilled = R.drawable.cloud_filled,
        requiresRoot = false
    ),

    Modules(
        direction = ModulesScreenDestination,
        label = R.string.page_modules,
        icon = R.drawable.keyframes,
        iconFilled = R.drawable.keyframes_filled,
        requiresRoot = true
    ),
    Settings(
        direction = SettingsScreenDestination,
        label = R.string.page_settings,
        icon = R.drawable.settings,
        iconFilled = R.drawable.settings_filled,
        requiresRoot = false
    ),
}