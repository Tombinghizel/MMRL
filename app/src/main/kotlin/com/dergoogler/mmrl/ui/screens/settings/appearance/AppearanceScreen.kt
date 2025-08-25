package com.dergoogler.mmrl.ui.screens.settings.appearance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.datastore.model.Homepage
import com.dergoogler.mmrl.ext.toFormattedDateSafely
import com.dergoogler.mmrl.ui.component.SettingsScaffold
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.RadioDialogItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.SwitchItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.TextEditDialogItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Description
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.DialogDescription
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.ui.navigation.MainGraph
import com.dergoogler.mmrl.ui.providable.LocalSettings
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ui.screens.settings.NavButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.AppThemeScreenDestination

@Destination<MainGraph>
@Composable
fun AppearanceScreen() {
    val viewModel = LocalSettings.current
    val userPreferences = LocalUserPreferences.current

    SettingsScaffold(
        title = R.string.settings_appearance
    ) {
        NavButton(
            title = R.string.settings_app_theme,
            desc = R.string.settings_app_theme_desc,
            route = AppThemeScreenDestination
        )

        TextEditDialogItem(
            value = userPreferences.datePattern,
            onConfirm = {
                viewModel.setDatePattern(it)
            }
        ) {
            Title(R.string.settings_date_pattern)
            Description(R.string.settings_date_pattern_desc)

            val date = System.currentTimeMillis().toFormattedDateSafely(it.value)
            DialogDescription(R.string.settings_date_pattern_dialog_desc, date)
        }

        RadioDialogItem(
            selection = userPreferences.homepage,
            options = listOf(
                RadioDialogItem(
                    value = Homepage.Home,
                    title = stringResource(R.string.page_home)
                ),
                RadioDialogItem(
                    value = Homepage.Repositories,
                    title = stringResource(R.string.page_repositorys)
                ),
                RadioDialogItem(
                    value = Homepage.Modules,
                    enabled = viewModel.isProviderAlive,
                    title = stringResource(R.string.page_modules)
                )
            ),
            onConfirm = {
                viewModel.setHomepage(it.value)
            }
        ) {
            Title(R.string.settings_homepage)
            Description(R.string.settings_homepage_desc)
        }

        SwitchItem(
            checked = userPreferences.enableToolbarEvents,
            onChange = viewModel::setEnableToolbarEvents
        ) {
            Title(R.string.settings_enable_toolbar_events)
        }
    }
}