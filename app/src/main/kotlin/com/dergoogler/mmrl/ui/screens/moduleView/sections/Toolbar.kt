package com.dergoogler.mmrl.ui.screens.moduleView.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.ext.repoId
import com.dergoogler.mmrl.ext.shareText
import com.dergoogler.mmrl.model.local.BulkModule
import com.dergoogler.mmrl.model.online.isValid
import com.dergoogler.mmrl.platform.content.isValid
import com.dergoogler.mmrl.ui.component.toolbar.Toolbar
import com.dergoogler.mmrl.ui.providable.LocalBulkInstall
import com.dergoogler.mmrl.ui.providable.LocalDestinationsNavigator
import com.dergoogler.mmrl.ui.providable.LocalModule
import com.dergoogler.mmrl.ui.providable.LocalOnlineModule
import com.dergoogler.mmrl.ui.providable.LocalRepo
import com.dergoogler.mmrl.ui.providable.LocalSnackbarHost
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ui.providable.LocalVersionItem
import com.dergoogler.mmrl.ui.screens.moduleView.items.VersionsItem
import com.dergoogler.mmrl.ui.screens.moduleView.providable.LocalModuleViewModel
import kotlinx.coroutines.launch

@Composable
internal fun Toolbar() {
    val context = LocalContext.current
    val viewModel = LocalModuleViewModel.current
    val repo = LocalRepo.current
    val userPreferences = LocalUserPreferences.current
    val module = LocalOnlineModule.current
    val lastVersionItem = LocalVersionItem.current
    val snackbarHostState = LocalSnackbarHost.current
    val bulkInstallViewModel = LocalBulkInstall.current
    val browser = LocalUriHandler.current
    val local = LocalModule.current
    val navigator = LocalDestinationsNavigator.current

    val scope = rememberCoroutineScope()
    val repositoryMenu = remember(userPreferences) { userPreferences.repositoryMenu }

    Toolbar(
        colors = TopAppBarDefaults.topAppBarColors().copy(
            scrolledContainerColor = Color.Transparent,
            containerColor = Color.Transparent
        ),
        title = {},
        navigationIcon = {
            IconButton(onClick = { navigator.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left), contentDescription = null
                )
            }
        },
        bottomBorder = !(module.hasCover && repositoryMenu.showCover),
        actions = {
            VersionsItem(
                count = viewModel.versions.size,
                onClick = {
                    viewModel.versionSelectBottomSheet = true
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = { viewModel.menuExpanded = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.dots_vertical),
                    contentDescription = null,
                )
            }
            DropdownMenu(
                expanded = viewModel.menuExpanded,
                onDismissRequest = { viewModel.menuExpanded = false }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.share),
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.view_module_share)
                        )
                    },
                    onClick = {
                        viewModel.menuExpanded = false
                        context.shareText("https://mmrl.dev/repository/${repo.url.repoId}/${module.id}?utm_medium=share&utm_source=${context.packageName}")
                    }
                )

                lastVersionItem.isValid {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.package_import),
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(id = R.string.bulk_add_as_bulk)
                            )
                        },
                        onClick = {
                            viewModel.menuExpanded = false
                            bulkInstallViewModel.addBulkModule(
                                module = BulkModule(
                                    id = module.id,
                                    name = module.name,
                                    versionItem = it
                                ),
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.bulk_install_module_added),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                onFailure = { error ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = error,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }
                    )
                }

                lastVersionItem.isValid {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.http_trace),
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(
                                text = "track.json"
                            )
                        },
                        onClick = {
                            viewModel.menuExpanded = false
                            browser.openUri("${it.repoUrl}modules/${module.id}/track.json")
                        }
                    )
                }

                local.isValid {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(
                                    id = if (viewModel.notifyUpdates) {
                                        R.drawable.target_off
                                    } else {
                                        R.drawable.target
                                    }
                                ),
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(
                                    id = if (viewModel.notifyUpdates) {
                                        R.string.view_module_update_ignore
                                    } else {
                                        R.string.view_module_update_notify
                                    }
                                )
                            )
                        },
                        onClick = {
                            viewModel.menuExpanded = false
                            viewModel.setUpdatesTag(!viewModel.notifyUpdates)
                        }
                    )
                }
            }
        },
    )
}