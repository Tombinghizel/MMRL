package com.dergoogler.mmrl.ui.screens.appProfile.items

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.platform.ksu.KsuNative
import com.dergoogler.mmrl.platform.ksu.Profile
import com.dergoogler.mmrl.ui.component.listItem.ListSwitchItem

@Composable
fun AppProfileConfig(
    modifier: Modifier = Modifier,
    fixedName: Boolean,
    enabled: Boolean,
    profile: Profile,
    onProfileChange: (Profile) -> Unit,
) {
    Column(modifier = modifier) {
        if (!fixedName) {
            OutlinedTextField(
                label = { Text("Profile Name") },
                value = profile.name,
                onValueChange = { onProfileChange(profile.copy(name = it)) }
            )
        }

        ListSwitchItem(
            title = stringResource(R.string.settings_umount_modules_default),
            desc = stringResource(R.string.settings_umount_modules_default_summary),
            checked = if (enabled) {
                profile.umountModules
            } else {
                KsuNative.isDefaultUmountModules()
            },
            enabled = enabled,
            onChange = {
                onProfileChange(
                    profile.copy(
                        umountModules = it,
                        nonRootUseDefault = false
                    )
                )
            }
        )
    }
}
