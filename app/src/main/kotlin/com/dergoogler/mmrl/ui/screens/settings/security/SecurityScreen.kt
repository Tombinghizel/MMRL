package com.dergoogler.mmrl.ui.screens.settings.security

import androidx.compose.runtime.Composable
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.ui.component.SettingsScaffold
import com.dergoogler.mmrl.ui.providable.LocalSettings
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.SwitchItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Description
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.ui.navigation.MainGraph
import com.ramcosta.composedestinations.annotation.Destination

@Destination<MainGraph>
@Composable
fun SecurityScreen() {
    val viewModel = LocalSettings.current
    val userPreferences = LocalUserPreferences.current

    SettingsScaffold(
        title = R.string.settings_security,
    ) {
        SwitchItem(
            checked = userPreferences.confirmReboot,
            onChange = viewModel::setConfirmReboot
        ) {
            Title(R.string.settings_reboot_protection)
            Description(R.string.settings_reboot_protection_desc)
        }

        SwitchItem(
            checked = userPreferences.blacklistAlerts,
            onChange = viewModel::setBlacklistAlerts

        ) {
            Title(R.string.settings_blacklist_alerts)
            Description(R.string.settings_blacklist_alerts_desc)
        }

        SwitchItem(
            checked = userPreferences.hideFingerprintInHome,
            onChange = viewModel::setHideFingerprintInHome
        ) {
            Title(R.string.settings_hide_fingerprint)
            Description(R.string.settings_hide_fingerprint_desc)
        }

        SwitchItem(
            checked = userPreferences.strictMode,
            onChange = viewModel::setStrictMode
        ) {
            Title(R.string.settings_strict_mode)
        }
    }
}