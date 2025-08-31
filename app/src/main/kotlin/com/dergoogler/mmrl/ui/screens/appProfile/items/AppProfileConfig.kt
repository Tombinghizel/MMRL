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


    }
}
