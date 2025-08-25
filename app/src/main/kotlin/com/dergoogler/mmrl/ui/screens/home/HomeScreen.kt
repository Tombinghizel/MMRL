package com.dergoogler.mmrl.ui.screens.home

import android.os.Build
import android.system.Os
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.datastore.model.WorkingMode.Companion.isNonRoot
import com.dergoogler.mmrl.datastore.model.WorkingMode.Companion.isRoot
import com.dergoogler.mmrl.ext.currentScreenWidth
import com.dergoogler.mmrl.ext.managerVersion
import com.dergoogler.mmrl.ext.none
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ext.takeTrue
import com.dergoogler.mmrl.model.online.Changelog
import com.dergoogler.mmrl.network.runRequest
import com.dergoogler.mmrl.platform.file.SuFile.Companion.toFormattedFileSize
import com.dergoogler.mmrl.platform.ksu.KsuNative
import com.dergoogler.mmrl.stub.IMMRLApiManager
import com.dergoogler.mmrl.ui.component.Alert
import com.dergoogler.mmrl.ui.component.SELinuxStatus
import com.dergoogler.mmrl.ui.component.TopAppBar
import com.dergoogler.mmrl.ui.component.TopAppBarEventIcon
import com.dergoogler.mmrl.ui.component.card.Card
import com.dergoogler.mmrl.ui.component.card.component.Relative
import com.dergoogler.mmrl.ui.component.listItem.dsl.List
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.Item
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Description
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Icon
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.ui.component.scaffold.Scaffold
import com.dergoogler.mmrl.ui.navigation.MainGraph
import com.dergoogler.mmrl.ui.providable.LocalDestinationsNavigator
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ui.screens.home.items.NonRootItem
import com.dergoogler.mmrl.ui.screens.home.items.RebootBottomSheet
import com.dergoogler.mmrl.ui.screens.home.items.RootItem
import com.dergoogler.mmrl.ui.screens.settings.changelogs.items.ChangelogBottomSheet
import com.dergoogler.mmrl.viewmodel.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ThankYouScreenDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

val listItemContentPaddingValues: PaddingValues = PaddingValues(vertical = 8.dp, horizontal = 25.dp)

@Destination<MainGraph>(start = true)
@OptIn(ExperimentalComposeApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navigator = LocalDestinationsNavigator.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val userPreferences = LocalUserPreferences.current
    val browser = LocalUriHandler.current

    var openRebootSheet by remember { mutableStateOf(false) }
    if (openRebootSheet) {
        RebootBottomSheet(
            onClose = { openRebootSheet = false })
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isProviderAlive = viewModel.isProviderAlive,
                onInfoClick = {
                    navigator.navigate(AboutScreenDestination)
                },
                onHeartClick = {
                    navigator.navigate(ThankYouScreenDestination)
                },
                onRebootClick = {
                    openRebootSheet = true
                },
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        this@Scaffold.ResponsiveContent {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when {
                    userPreferences.workingMode.isRoot -> RootItem(
                        developerMode = userPreferences.developerMode,
                        viewModel = viewModel,
                    )

                    userPreferences.workingMode.isNonRoot -> NonRootItem(
                        developerMode = userPreferences.developerMode,
                    )
                }

                if (userPreferences.checkAppUpdates) {
                    var changelog by remember { mutableStateOf<List<Changelog>?>(null) }
                    LaunchedEffect(Unit) {
                        runRequest {
                            withContext(Dispatchers.IO) {
                                val api = IMMRLApiManager.build()
                                return@withContext api.changelog.execute()
                            }
                        }.onSuccess { list ->
                            changelog = list
                        }.onFailure {
                            Timber.e(it, "unable to get changelog")
                        }

                    }

                    changelog?.let {
                        val latest = it.first()

                        var changelogSheet by remember { mutableStateOf(false) }
                        if (changelogSheet) {
                            ChangelogBottomSheet(
                                changelog = latest,
                                onClose = { changelogSheet = false })
                        }

                        AnimatedVisibility(
                            visible = userPreferences.developerMode { devAlwaysShowUpdateAlert } || (if (latest.preRelease) {
                                userPreferences.checkAppUpdatesPreReleases && latest.versionCode > context.managerVersion.second
                            } else {
                                latest.versionCode > context.managerVersion.second
                            }),
                            enter = fadeIn() + expandVertically(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Alert(
                                onClick = {
                                    changelogSheet = true
                                },
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                title = stringResource(R.string.update_available),
                                message = stringResource(
                                    R.string.new_version_available,
                                    latest.versionName
                                ),
                                icon = R.drawable.cloud_download,
                            )
                        }
                    }
                }

                List(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = listItemContentPaddingValues
                ) {
                    val scope = this

                    Card(
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        val uname = Os.uname()
                        Column(
                            modifier = Modifier.relative()
                        ) {
                            scope.Item {
                                Icon(painter = painterResource(R.drawable.cookie_man))
                                Title(R.string.kernel)
                                Description(uname.release)
                            }

                            scope.Item {
                                Icon(painter = painterResource(R.drawable.launcher_outline))
                                Title(R.string.manager_version)
                                Description("${context.managerVersion.first} (${context.managerVersion.second})")
                            }

                            scope.Item {
                                Icon(painter = painterResource(R.drawable.fingerprint))
                                Title(R.string.fingerprint)
                                Description(
                                    if (userPreferences.hideFingerprintInHome) {
                                        stringResource(id = R.string.hidden)
                                    } else {
                                        Build.FINGERPRINT
                                    }
                                )
                            }
                            scope.Item {
                                Icon(painter = painterResource(R.drawable.cpu_2))
                                Title(R.string.architecture)
                                Description(uname.machine)
                            }

                            scope.SELinuxStatus()

                            viewModel.platform.isKernelSuOrNext.takeTrue {
                                scope.Item {
                                    Icon(painter = painterResource(R.drawable.user_outlined))
                                    Title(R.string.super_user_apps)
                                    Description(viewModel.superUserCount.toString())
                                }
                            }
                        }
                    }

                    viewModel.isProviderAlive.takeTrue {
                        userPreferences.developerMode.takeTrue {
                            Card(
                                modifier = Modifier.padding(vertical = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.relative()
                                ) {
                                    scope.Item {
                                        Title(R.string.home_root_provider_version_name)
                                        Description(viewModel.versionName)
                                    }

                                    scope.Item {
                                        Title(R.string.home_root_provider_se_linux_context)
                                        Description(viewModel.seLinuxContext)
                                    }

                                    if (viewModel.platform.isKernelSuNext) {
                                        KsuNative.getHookMode().nullable {
                                            scope.Item {
                                                Title(R.string.hook_mode)
                                                Description(it)
                                            }
                                        }
                                    }

                                    if (viewModel.platform.isSukiSU) {
                                        KsuNative.getHookType().nullable {
                                            scope.Item {
                                                Title(R.string.hook_mode)
                                                Description(it)
                                            }
                                        }

                                        scope.Item {
                                            Title(R.string.kpm_support)
                                            Description(if (KsuNative.isKPMEnabled()) R.string.yes else R.string.no)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (viewModel.platform.isValid) {
                        viewModel.analytics(context).nullable {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .weight(1f)
                                ) {
                                    Relative {
                                        scope.Item {
                                            Title(R.string.home_installed_modules)
                                            Description(it.totalModules.toString())
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .weight(1f)
                                ) {
                                    Relative {
                                        scope.Item {
                                            Title(R.string.home_updated_modules)
                                            Description(it.totalUpdated.toString())
                                        }
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                            ) {
                                Relative {
                                    scope.Item {
                                        Title(R.string.home_storage_usage)

                                        Description {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = it.totalModulesUsageBytes.toFormattedFileSize(),
                                                )

                                                LinearProgressIndicator(
                                                    progress = {
                                                        it.totalStorageUsage
                                                    },
                                                    modifier = Modifier
                                                        .height(10.dp)
                                                        .weight(1f),
                                                    drawStopIndicator = {}
                                                )

                                                Text(
                                                    text = it.totalDeviceStorageBytes.toFormattedFileSize(),
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .weight(1f)
                                ) {
                                    Relative {
                                        scope.Item {
                                            Title(R.string.home_enabled_modules)
                                            Description(it.totalEnabled.toString())
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .weight(1f)
                                ) {
                                    Relative {
                                        scope.Item {
                                            Title(R.string.home_disabled_modules)
                                            Description(it.totalDisabled.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        onClick = {
                            browser.openUri("https://github.com/sponsors/DerGoogler")
                        }
                    ) {
                        Relative {
                            scope.Item {
                                Title(
                                    id = R.string.home_support_title,
                                    styleTransform = {
                                        val newStyle = it.copy(color = Color.Unspecified)
                                        it.merge(newStyle)
                                    }
                                )

                                Description(
                                    id = R.string.home_support_content,
                                    styleTransform = {
                                        val newStyle = it.copy(color = Color.Unspecified)
                                        it.merge(newStyle)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    isProviderAlive: Boolean,
    onRebootClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onHeartClick: () -> Unit = {},
) {
    val width = currentScreenWidth()

    TopAppBar(
        title = {
            if (!width.isSmall) return@TopAppBar

            TopAppBarEventIcon()
        },
        actions = {
            if (isProviderAlive) {
                IconButton(onClick = onRebootClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.refresh),
                        contentDescription = null
                    )
                }
            }

            IconButton(onClick = onHeartClick) {
                Icon(
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = null
                )
            }

            IconButton(onClick = onInfoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.info_circle),
                    contentDescription = null
                )
            }
        }
    )
}
