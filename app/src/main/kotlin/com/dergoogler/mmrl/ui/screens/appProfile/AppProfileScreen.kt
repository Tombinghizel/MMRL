package com.dergoogler.mmrl.ui.screens.appProfile

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.compose.AsyncImage
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.platform.ksu.KsuNative
import com.dergoogler.mmrl.platform.ksu.Profile
import com.dergoogler.mmrl.ui.component.listItem.ListSwitchItem
import com.dergoogler.mmrl.ui.providable.LocalDestinationsNavigator
import com.dergoogler.mmrl.ui.providable.LocalMainScreenInnerPaddings
import com.dergoogler.mmrl.ui.providable.LocalSnackbarHost
import com.dergoogler.mmrl.ui.providable.LocalSuperUserViewModel
import com.dergoogler.mmrl.ui.screens.appProfile.items.AppProfileConfig
import com.dergoogler.mmrl.ui.screens.appProfile.items.RootProfileConfig
import com.dergoogler.mmrl.utils.SePolicy.getSepolicy
import com.dergoogler.mmrl.viewmodel.SuperUserViewModel
import com.dergoogler.mmrl.viewmodel.SuperUserViewModel.AppInfo.Companion.loadIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun AppProfileScreen(
    appInfo: SuperUserViewModel.AppInfo,
) {
    val navigator = LocalDestinationsNavigator.current
    val context = LocalContext.current
    val snackBarHost = LocalSnackbarHost.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val viewModel = LocalSuperUserViewModel.current
    val failToUpdateAppProfile =
        stringResource(R.string.failed_to_update_app_profile, appInfo.label)
    val failToUpdateSepolicy =
        stringResource(R.string.failed_to_update_sepolicy, appInfo.label)
    val suNotAllowed = stringResource(R.string.su_not_allowed, appInfo.label)

    val packageName = appInfo.packageName
    val initialProfile = KsuNative.getAppProfile(packageName, appInfo.uid)
    if (initialProfile.allowSu) {
        initialProfile.rules = getSepolicy(packageName)
    }
    var profile by rememberSaveable {
        mutableStateOf(initialProfile)
    }

    val icon = remember {
        appInfo.loadIcon(context)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                onBack = dropUnlessResumed { navigator.popBackStack() },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHost) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        AppProfileInner(
            modifier = Modifier
                .padding(paddingValues),
            packageName = appInfo.packageName,
            appLabel = appInfo.label,
            appIcon = {
                AsyncImage(
                    model = icon,
                    contentDescription = appInfo.label,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(48.dp)
                )
            },
            profile = profile,
            onProfileChange = {
                scope.launch {
                    if (it.allowSu) {
                        // sync with allowlist.c - forbid_system_uid
                        if (appInfo.uid < 2000 && appInfo.uid != 1000) {
                            snackBarHost.showSnackbar(suNotAllowed)
                            return@launch
                        }
                        if (!it.rootUseDefault && it.rules.isNotEmpty() /*&& !setSepolicy(
                                profile.name,
                                it.rules
                            )*/
                        ) {
                            snackBarHost.showSnackbar(failToUpdateSepolicy)
                            return@launch
                        }
                    }
                    if (!KsuNative.setAppProfile(it)) {
                        snackBarHost.showSnackbar(failToUpdateAppProfile.format(appInfo.uid))
                    } else {
                        profile = it
                        viewModel.updateAppProfile(packageName, it)
                    }
                }
            },
        )
    }
}

@Composable
private fun AppProfileInner(
    modifier: Modifier = Modifier,
    packageName: String,
    appLabel: String,
    appIcon: @Composable () -> Unit,
    profile: Profile,
    onProfileChange: (Profile) -> Unit,
) {
    val bottomBarPaddingValues = LocalMainScreenInnerPaddings.current
    val isRootGranted = profile.allowSu

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = appLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            supportingContent = { Text(packageName) },
            leadingContent = appIcon,
        )

        ListSwitchItem(
            title = stringResource(id = R.string.allow_root),
            checked = isRootGranted,
            onChange = { onProfileChange(profile.copy(allowSu = it)) },
        )

        val initialMode = if (profile.rootUseDefault) {
            Mode.Default
        } else {
            Mode.Custom
        }

        var mode by rememberSaveable {
            mutableStateOf(initialMode)
        }

        Crossfade(targetState = isRootGranted, label = "") { current ->
            Column(
                modifier = Modifier.padding(bottom = 6.dp + 48.dp + 6.dp /* SnackBar height */)
            ) {
                if (current) {

                    ProfileBox(mode) {
                        // template mode shouldn't change profile here!
                        if (it == Mode.Default || it == Mode.Custom) {
                            onProfileChange(profile.copy(rootUseDefault = it == Mode.Default))
                        }
                        mode = it
                    }
                    Crossfade(targetState = mode, label = "") { currentMode ->
                        if (currentMode == Mode.Custom) {
                            RootProfileConfig(
                                fixedName = true,
                                profile = profile,
                                onProfileChange = onProfileChange
                            )
                        }
                    }
                } else {
                    val mode = if (profile.nonRootUseDefault) Mode.Default else Mode.Custom
                    ProfileBox(mode) {
                        onProfileChange(profile.copy(nonRootUseDefault = (it == Mode.Default)))
                    }
                    Crossfade(targetState = mode, label = "") { currentMode ->
                        val modifyEnabled = currentMode == Mode.Custom
                        AppProfileConfig(
                            fixedName = true,
                            profile = profile,
                            enabled = modifyEnabled,
                            onProfileChange = onProfileChange
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(bottomBarPaddingValues.calculateBottomPadding()))
    }
}

private enum class Mode(@StringRes private val res: Int) {
    Default(R.string.profile_default),
    Custom(R.string.profile_custom);

    val text: String
        @Composable get() = stringResource(res)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.profile),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ProfileBox(
    mode: Mode,
    onModeChange: (Mode) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.profile),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = { Text(mode.text) },
        leadingContent = { Icon(Icons.Filled.AccountCircle, null) },
    )

    HorizontalDivider(thickness = Dp.Hairline)

    ListItem(headlineContent = {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = mode == Mode.Default,
                label = { Text(stringResource(R.string.profile_default)) },
                onClick = { onModeChange(Mode.Default) },
            )
            FilterChip(
                selected = mode == Mode.Custom,
                label = { Text(stringResource(R.string.profile_custom)) },
                onClick = { onModeChange(Mode.Custom) },
            )
        }
    })
}