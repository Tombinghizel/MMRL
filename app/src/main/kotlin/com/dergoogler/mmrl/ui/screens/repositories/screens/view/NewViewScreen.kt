package com.dergoogler.mmrl.ui.screens.repositories.screens.view

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.model.local.BulkModule
import com.dergoogler.mmrl.model.local.State
import com.dergoogler.mmrl.model.online.VersionItem
import com.dergoogler.mmrl.model.online.hasBlacklist
import com.dergoogler.mmrl.model.online.hasCategories
import com.dergoogler.mmrl.model.online.hasScreenshots
import com.dergoogler.mmrl.model.online.hasValidMessage
import com.dergoogler.mmrl.model.online.isBlacklisted
import com.dergoogler.mmrl.ui.activity.ScreenshotsPreviewActivity
import com.dergoogler.mmrl.ui.activity.terminal.install.InstallActivity
import com.dergoogler.mmrl.ui.component.Alert
import com.dergoogler.mmrl.ui.component.AntiFeaturesItem
import com.dergoogler.mmrl.ui.component.Cover
import com.dergoogler.mmrl.ui.component.LabelItem
import com.dergoogler.mmrl.ui.component.Logo
import com.dergoogler.mmrl.ui.component.PermissionItem
import com.dergoogler.mmrl.ui.component.text.TextWithIcon
import com.dergoogler.mmrl.ui.component.TopAppBar
import com.dergoogler.mmrl.ui.component.listItem.ListButtonItem
import com.dergoogler.mmrl.ui.component.listItem.ListCollapseItem
import com.dergoogler.mmrl.ui.component.listItem.ListItem
import com.dergoogler.mmrl.ui.component.listItem.ListItemTextStyle
import com.dergoogler.mmrl.ui.navigation.graphs.RepositoriesScreen
import com.dergoogler.mmrl.ui.providable.LocalPanicArguments
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.items.InstallConfirmDialog
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.items.LicenseItem
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.items.VersionsItem
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.items.ViewTrackBottomSheet
import com.dergoogler.mmrl.ui.screens.settings.blacklist.items.BlacklistBottomSheet
import com.dergoogler.mmrl.viewmodel.ModuleViewModel
import com.dergoogler.mmrl.viewmodel.RepositoryViewModel
import com.dergoogler.mmrl.ext.navigateSingleTopTo
import com.dergoogler.mmrl.ext.none
import com.dergoogler.mmrl.ext.panicString
import com.dergoogler.mmrl.ext.fadingEdge
import com.dergoogler.mmrl.ext.ifNotEmpty
import com.dergoogler.mmrl.ext.ifNotNullOrBlank
import com.dergoogler.mmrl.ext.isNotNullOrBlank
import com.dergoogler.mmrl.ext.isNullOrFalse
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ext.repoId
import com.dergoogler.mmrl.ext.shareText
import com.dergoogler.mmrl.ext.systemBarsPaddingEnd
import com.dergoogler.mmrl.ext.takeTrue
import com.dergoogler.mmrl.platform.file.SuFile.Companion.toFormattedFileSize
import com.dergoogler.mmrl.ui.component.listItem.dsl.List
import com.dergoogler.mmrl.ui.component.listItem.dsl.ListItemSlot
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.ButtonItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.CollapseItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.Item
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Description
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Icon
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Labels
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.ui.component.scaffold.Scaffold
import com.dergoogler.mmrl.ui.component.text.TextWithIconDefaults
import com.dergoogler.mmrl.ui.providable.LocalBulkInstall
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.items.OtherSourcesItem
import com.dergoogler.mmrl.utils.toFormattedDateSafely
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun NewViewScreen(
    viewModel: ModuleViewModel,
    navController: NavHostController,
) {
    val bulkInstallViewModel = LocalBulkInstall.current
    val userPreferences = LocalUserPreferences.current
    val repositoryMenu = userPreferences.repositoryMenu
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val module = viewModel.online
    val moduleAll by viewModel.onlineAll.collectAsStateWithLifecycle()
    val local = viewModel.local
    val lastVersionItem = viewModel.lastVersionItem
    val context = LocalContext.current
    val density = LocalDensity.current
    val browser = LocalUriHandler.current
    val arguments = LocalPanicArguments.current
    val repoUrl = arguments.panicString("repoUrl")

    val listItemContentPaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
    val subListItemContentPaddingValues = PaddingValues(vertical = 8.dp, horizontal = 16.dp)

    val screenshotsLazyListState = rememberLazyListState()
    val categoriesLazyListState = rememberLazyListState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var installConfirm by remember { mutableStateOf(false) }

    val download: (VersionItem, Boolean) -> Unit = { item, install ->
        viewModel.downloader(context, item) {
            if (install) {
                installConfirm = false
                InstallActivity.start(
                    context = context,
                    uri = it.toUri(),
                    confirm = false
                )
            }
        }
    }

    val subListItemStyle = ListItemTextStyle(
        titleTextColor = LocalContentColor.current,
        descTextColor = MaterialTheme.colorScheme.outline,
        titleTextStyle = MaterialTheme.typography.bodyMedium,
        descTextStyle = MaterialTheme.typography.bodyMedium,
        iconSize = 20.dp
    )

    var menuExpanded by remember { mutableStateOf(false) }

    val manager = module.manager(viewModel.platform)
    val requires = manager.require?.let {
        moduleAll.filter { onlineModules ->
            onlineModules.second.id in it
        }.map { it2 -> it2.second }
    } ?: emptyList()

    if (installConfirm) InstallConfirmDialog(
        name = module.name,
        requires = requires,
        onClose = {
            installConfirm = false
        },
        onConfirm = {
            lastVersionItem?.let { download(it, true) }
        },
        onConfirmDeps = {
            lastVersionItem?.let { item ->
                val bulkModules = mutableListOf<BulkModule>()
                bulkModules.add(
                    BulkModule(
                        id = module.id,
                        name = module.name,
                        versionItem = item
                    )
                )
                bulkModules.addAll(requires.map { r ->
                    BulkModule(
                        id = r.id,
                        name = r.name,
                        versionItem = r.versions.first()
                    )
                })

                bulkModules.ifNotEmpty {
                    bulkInstallViewModel.downloadMultiple(
                        items = bulkModules,
                        onAllSuccess = { uris ->
                            installConfirm = false
                            InstallActivity.start(
                                context = context,
                                uri = uris,
                                confirm = false
                            )
                        },
                        onFailure = { err ->
                            installConfirm = false
                            Timber.e(err)
                        }
                    )
                }
            }
        }
    )

    val isBlacklisted by module.isBlacklisted

    var versionSelectBottomSheet by remember { mutableStateOf(false) }
    if (versionSelectBottomSheet) VersionSelectBottomSheet(
        onClose = { versionSelectBottomSheet = false },
        versions = viewModel.versions,
        localVersionCode = viewModel.localVersionCode,
        isProviderAlive = viewModel.isProviderAlive,
        getProgress = { viewModel.getProgress(it) },
        onDownload = download,
        isBlacklisted = isBlacklisted
    )

    var viewTrackBottomSheet by remember { mutableStateOf(false) }
    if (viewTrackBottomSheet) ViewTrackBottomSheet(
        onClose = { viewTrackBottomSheet = false },
        tracks = viewModel.tracks
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    containerColor = Color.Transparent
                ),
                actions = {
                    VersionsItem(
                        count = viewModel.versions.size,
                        onClick = {
                            versionSelectBottomSheet = true
                        }
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.dots_vertical),
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
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
                                menuExpanded = false
                                context.shareText("https://mmrl.dev/repository/${repoUrl.repoId}/${module.id}?utm_medium=share&utm_source=${context.packageName}")
                            }
                        )

                        lastVersionItem?.let {
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
                                    menuExpanded = false
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

                        lastVersionItem?.let {
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
                                    menuExpanded = false
                                    browser.openUri("${it.repoUrl}modules/${module.id}/track.json")
                                }
                            )
                        }

                        local?.let {
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
                                    menuExpanded = false
                                    viewModel.setUpdatesTag(!viewModel.notifyUpdates)
                                }
                            )
                        }
                    }
                },
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        this@Scaffold.ResponsiveContent {
            Column(
                modifier = Modifier
                    .let {
                        if (repositoryMenu.showCover && module.hasCover) {
                            Modifier
                        } else {
                            it.padding(innerPadding)
                        }
                    }
                    .verticalScroll(rememberScrollState())
            ) {
                module.cover.nullable(repositoryMenu.showCover) {
                    if (it.isNotEmpty()) {
                        Cover(
                            modifier = Modifier.fadingEdge(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black
                                    ),
                                    startY = Float.POSITIVE_INFINITY,
                                    endY = 0f
                                )
                            ),
                            url = it,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .systemBarsPaddingEnd(),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        if (repositoryMenu.showIcon) {
                            if (module.icon.isNotNullOrBlank()) {
                                AsyncImage(
                                    model = module.icon,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(20)),
                                    contentDescription = null
                                )
                            } else {
                                Logo(
                                    icon = R.drawable.box,
                                    modifier = Modifier.size(60.dp),
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(20)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            TextWithIcon(
                                style = TextWithIconDefaults.style.copy(
                                    textStyle = MaterialTheme.typography.titleLarge,
                                    iconTint = MaterialTheme.colorScheme.surfaceTint,
                                    iconScaling = 1.0f,
                                    rightIcon = true,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                ),
                                text = module.name,
                                icon = module.isVerified nullable R.drawable.rosette_discount_check,
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        navController.navigateSingleTopTo(
                                            route = RepositoriesScreen.RepoSearch.route,
                                            args = mapOf(
                                                "type" to "author",
                                                "value" to module.author,
                                                "repoUrl" to repoUrl
                                            )
                                        )
                                    }
                                ),
                                text = module.author,
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surfaceTint),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        local?.let {
                            val ops by remember(
                                userPreferences.useShellForModuleStateChange,
                                it,
                                it.state
                            ) {
                                derivedStateOf {
                                    viewModel.createModuleOps(
                                        userPreferences.useShellForModuleStateChange,
                                        it
                                    )
                                }
                            }

                            OutlinedButton(
                                enabled = viewModel.isProviderAlive && (!userPreferences.useShellForModuleStateChange || it.state != State.REMOVE),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                onClick = ops.change
                            ) {
                                val style = LocalTextStyle.current
                                val progressSize =
                                    with(density) { style.fontSize.toDp() * 1.0f }

                                if (ops.isOpsRunning) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(progressSize),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = stringResource(
                                            id = if (it.state == State.REMOVE) {
                                                R.string.module_restore
                                            } else {
                                                R.string.module_remove
                                            }
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        val buttonTextResId = when {
                            local == null -> R.string.module_install
                            lastVersionItem != null && module.versionCode > local.versionCode -> R.string.module_update
                            else -> R.string.module_reinstall
                        }

                        Button(
                            enabled = viewModel.isProviderAlive && lastVersionItem != null && ! isBlacklisted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            onClick = {
                                installConfirm = true
                            },
                        ) {
                            Text(
                                text = stringResource(id = buttonTextResId),
                                maxLines = 1
                            )
                        }
                    }

                    val progress = lastVersionItem?.let {
                        viewModel.getProgress(it)
                    } ?: 0f

                    if (progress != 0f) {
                        LinearProgressIndicator(
                            progress = { progress },
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .height(0.9.dp)
                                .fillMaxWidth()
                        )
                    } else {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 0.9.dp
                        )
                    }

                    val alertPadding = PaddingValues(horizontal = 16.dp)

                    module.hasBlacklist {
                        var open by remember { mutableStateOf(false) }
                        if (open) {
                            BlacklistBottomSheet(
                                module = it,
                                onClose = { open = false })
                        }

                        Alert(
                            icon = R.drawable.alert_circle_filled,
                            title = stringResource(R.string.blacklisted),
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            textColor = MaterialTheme.colorScheme.onErrorContainer,
                            message = stringResource(R.string.blacklisted_desc),
                            outsideContentPadding = alertPadding,
                        )
                    }

                    manager.isNotSupportedRootVersion(viewModel.versionCode) { min ->
                        if (min == -1) {
                            Alert(
                                title = stringResource(id = R.string.view_module_unsupported),
                                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                textColor = MaterialTheme.colorScheme.onErrorContainer,
                                message = stringResource(id = R.string.view_module_unsupported_desc),
                                outsideContentPadding = alertPadding,
                            )
                        } else {
                            Alert(
                                title = stringResource(id = R.string.view_module_low_root_version),
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                message = stringResource(id = R.string.view_module_low_root_version_desc),
                                outsideContentPadding = alertPadding,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    manager.isNotSupportedDevice {
                        Alert(
                            title = stringResource(id = R.string.view_module_unsupported_device),
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            textColor = MaterialTheme.colorScheme.onErrorContainer,
                            message = stringResource(id = R.string.view_module_unsupported_device_desc),
                            outsideContentPadding = alertPadding,
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    manager.isNotSupportedArch {
                        Alert(
                            title = stringResource(id = R.string.view_module_unsupported_arch),
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            textColor = MaterialTheme.colorScheme.onErrorContainer,
                            message = stringResource(id = R.string.view_module_unsupported_arch_desc),
                            outsideContentPadding = alertPadding,
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    module.note.hasValidMessage {
                        if (it.hasTitle && it.isDeprecated) {
                            Alert(
                                icon = R.drawable.alert_triangle,
                                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                textColor = MaterialTheme.colorScheme.onErrorContainer,
                                title = it.title,
                                message = it.message!!,
                                outsideContentPadding = alertPadding,
                            )
                        } else {
                            Alert(
                                title = it.title,
                                message = it.message!!,
                                outsideContentPadding = alertPadding,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    List(
                        contentPadding = listItemContentPaddingValues
                    ) {
                        if (!module.readme.isNullOrBlank()) {
                            ButtonItem(
                                onClick = {
                                    navController.navigateSingleTopTo(
                                        route = RepositoriesScreen.Description.route,
                                        args = mapOf(
                                            "moduleId" to module.id,
                                            "repoUrl" to repoUrl
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    slot = ListItemSlot.End,
                                    painter = painterResource(id = R.drawable.arrow_right)
                                )
                                Title(R.string.view_module_about_this_module)
                            }
                        } else {
                            Item {
                                Title(R.string.view_module_about_this_module)
                            }
                        }
                    }

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = module.description
                            ?: stringResource(R.string.view_module_no_description),
                        style = MaterialTheme.typography.bodyMedium.apply {
                            if (module.description.isNullOrBlank()) {
                                copy(
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        },
                        color = MaterialTheme.colorScheme.outline
                    )

                    module.hasCategories {
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            state = categoriesLazyListState,
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
                        ) {
                            items(it.size) { category ->
                                AssistChip(
                                    onClick = {
                                        navController.navigateSingleTopTo(
                                            route = RepositoriesScreen.RepoSearch.route,
                                            args = mapOf(
                                                "type" to "category",
                                                "value" to it[category],
                                                "repoUrl" to repoUrl
                                            )
                                        )
                                    },
                                    label = { Text(it[category]) }
                                )
                            }
                        }
                    }

                    module.hasScreenshots { screens ->
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            state = screenshotsLazyListState,
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)

                        ) {
                            itemsIndexed(
                                items = screens,
                            ) { index, screen ->
                                val interactionSource = remember { MutableInteractionSource() }

                                AsyncImage(
                                    model = screen,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(160.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = ripple(),
                                            onClick = {
                                                ScreenshotsPreviewActivity.start(
                                                    context,
                                                    screens,
                                                    index
                                                )
                                            }
                                        )
                                        .aspectRatio(9f / 16f)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    List(
                        contentPadding = listItemContentPaddingValues
                    ) {
                        CollapseItem(
                            meta = { icon, rotation ->
                                Title(R.string.view_module_module_support)
                                Icon(
                                    slot = ListItemSlot.End,
                                    modifier = Modifier
                                        .graphicsLayer(rotationZ = rotation),
                                    painter = painterResource(id = icon),
                                )
                            },
                        ) {
                            module.donate.ifNotNullOrBlank {
                                ButtonItem(
                                    contentPadding = subListItemContentPaddingValues,
                                    onClick = {
                                        browser.openUri(it)
                                    }
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.currency_dollar))
                                    Title(R.string.view_module_donate)
                                    Description(R.string.view_module_donate_desc)
                                }
                            }

                            ButtonItem(
                                contentPadding = subListItemContentPaddingValues,
                                onClick = {
                                    browser.openUri(module.track.source)
                                }
                            ) {
                                Icon(painter = painterResource(id = R.drawable.brand_git))
                                Title(R.string.view_module_source)
                            }

                            module.homepage.ifNotNullOrBlank {
                                ButtonItem(
                                    contentPadding = subListItemContentPaddingValues,
                                    onClick = {
                                        browser.openUri(it)
                                    }
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.world_www))
                                    Title(R.string.view_module_homepage)
                                }
                            }

                            module.support?.ifNotNullOrBlank {
                                ButtonItem(
                                    contentPadding = subListItemContentPaddingValues,
                                    onClick = {
                                        browser.openUri(it)
                                    }
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.heart_handshake))
                                    Title(R.string.view_module_support)
                                }
                            }
                        }

                        module.permissions.ifNotEmpty {
                            CollapseItem(
                                meta = { icon, rotation ->
                                    Title(R.string.view_module_permissions)
                                    Icon(
                                        slot = ListItemSlot.End,
                                        modifier = Modifier
                                            .graphicsLayer(rotationZ = rotation),
                                        painter = painterResource(id = icon),
                                    )
                                    Labels {
                                        LabelItem(
                                            text = stringResource(
                                                R.string.view_module_section_count,
                                                it.size
                                            )
                                        )
                                    }
                                },
                            ) {
                                PermissionItem(
                                    contentPadding = subListItemContentPaddingValues,
                                    permissions = it
                                )
                            }
                        }

                        module.track.antifeatures.ifNotEmpty {
                            CollapseItem(
                                meta = { icon, rotation ->
                                    Title(R.string.view_module_antifeatures)
                                    Icon(
                                        slot = ListItemSlot.End,
                                        modifier = Modifier
                                            .graphicsLayer(rotationZ = rotation),
                                        painter = painterResource(id = icon),
                                    )
                                    Labels {
                                        LabelItem(
                                            text = stringResource(
                                                R.string.view_module_section_count,
                                                it.size
                                            )
                                        )
                                    }
                                },
                            ) {
                                AntiFeaturesItem(
                                    contentPadding = subListItemContentPaddingValues,
                                    antifeatures = it
                                )
                            }
                        }

                        requires.ifNotEmpty { requiredIds ->
                            CollapseItem(
                                meta = { icon, rotation ->
                                    Title(R.string.view_module_dependencies)
                                    Icon(
                                        slot = ListItemSlot.End,
                                        modifier = Modifier
                                            .graphicsLayer(rotationZ = rotation),
                                        painter = painterResource(id = icon),
                                    )
                                    Labels {
                                        LabelItem(
                                            text = stringResource(
                                                R.string.view_module_section_count,
                                                requiredIds.size
                                            )
                                        )
                                    }
                                },
                            ) {
                                requiredIds.forEach { onlineModule ->
                                    // val parts = requiredId.split("@")

                                    // val id = parts[0]
                                    // val version = (parts.getOrElse(1) { "-1" }).toInt()

                                    ButtonItem(
                                        contentPadding = subListItemContentPaddingValues,
                                        onClick = {
                                            //                                navController.navigateSingleTopTo(
//                                    ModuleViewModel.putModule(onlineModule, moduleArgs.url),
//                                    launchSingleTop = false
//                                )
                                        }
                                    ) {
                                        Title(onlineModule.name)
                                        Description(onlineModule.versionCode.toString())
                                    }
                                }
                            }
                        }

                        viewModel.otherSources.ifNotEmpty {
                            Item {
                                Title(R.string.from_other_repositories)
                            }

                            OtherSourcesItem(viewModel.otherSources)
                        }
                    }

                    // Information section
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 0.9.dp
                    )

                    userPreferences.developerMode.takeTrue {
                        ModuleInfoListItem(
                            title = R.string.view_module_module_id,
                            desc = module.id
                        )
                    }

                    module.license.ifNotNullOrBlank {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        ) {
                            Text(
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
                                modifier = Modifier.weight(1f),
                                text = stringResource(id = R.string.view_module_license)
                            )
                            LicenseItem(licenseId = it)
                        }
                    }
                    ModuleInfoListItem(
                        title = R.string.view_module_version,
                        desc = "${module.version} (${module.versionCode})"
                    )
                    lastVersionItem?.let {
                        ModuleInfoListItem(
                            title = R.string.view_module_last_updated,
                            desc = it.timestamp.toFormattedDateSafely
                        )
                    }
                    module.size?.let {
                        ModuleInfoListItem(
                            title = R.string.view_module_file_size,
                            desc = it.toFormattedFileSize()
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.view_module_provided_by)
                        )

                        Text(
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surfaceTint),
                            modifier = Modifier.clickable(
                                onClick = { viewTrackBottomSheet = true }
                            ),
                            text = viewModel.repo.name,
                        )
                    }

                    manager.min?.let {
                        ModuleInfoListItem(
                            title = R.string.view_module_required_root_version,
                            desc = it.toString()
                        )
                    }

                    module.minApi?.let {
                        ModuleInfoListItem(
                            title = R.string.view_module_required_os,
                            desc = stringResource(
                                R.string.view_module_required_os_value, when (it) {
                                    Build.VERSION_CODES.JELLY_BEAN -> "4.1"
                                    Build.VERSION_CODES.JELLY_BEAN_MR1 -> "4.2"
                                    Build.VERSION_CODES.JELLY_BEAN_MR2 -> "4.3"
                                    Build.VERSION_CODES.KITKAT -> "4.4"
                                    Build.VERSION_CODES.KITKAT_WATCH -> "4.4"
                                    Build.VERSION_CODES.LOLLIPOP -> "5.0"
                                    Build.VERSION_CODES.LOLLIPOP_MR1 -> "5.1"
                                    Build.VERSION_CODES.M -> "6.0"
                                    Build.VERSION_CODES.N -> "7.0"
                                    Build.VERSION_CODES.N_MR1 -> "7.1"
                                    Build.VERSION_CODES.O -> "8.0"
                                    Build.VERSION_CODES.O_MR1 -> "8.1"
                                    Build.VERSION_CODES.P -> "9.0"
                                    Build.VERSION_CODES.Q -> "10"
                                    Build.VERSION_CODES.R -> "11"
                                    Build.VERSION_CODES.S -> "12"
                                    Build.VERSION_CODES.S_V2 -> "12"
                                    Build.VERSION_CODES.TIRAMISU -> "13"
                                    Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> "14"
                                    else -> "[Sdk: $it]"
                                }
                            )
                        )
                    }

                    module.track.added?.let {
                        ModuleInfoListItem(
                            title = R.string.view_module_added_on,
                            desc = it.toFormattedDateSafely
                        )
                    }


                    local?.let { loc ->
                        List(
                            contentPadding = PaddingValues(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )
                        ) {
                            CollapseItem(
                                meta = { icon, rotation ->
                                    Title(
                                        id = R.string.module_installed,
                                        styleTransform = {
                                            val newStyle =
                                                MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.surfaceTint)

                                            it.merge(newStyle)
                                        }
                                    )
                                    Icon(
                                        slot = ListItemSlot.End,
                                        modifier = Modifier
                                            .graphicsLayer(rotationZ = rotation),
                                        painter = painterResource(id = icon),
                                    )
                                }
                            ) {
                                userPreferences.developerMode.takeTrue {
                                    ModuleInfoListItem(
                                        title = R.string.view_module_module_id,
                                        desc = loc.id.toString()
                                    )
                                }

                                ModuleInfoListItem(
                                    title = R.string.view_module_version,
                                    desc = "${loc.version} (${loc.versionCode})"
                                )
                                ModuleInfoListItem(
                                    title = R.string.view_module_last_updated,
                                    desc = loc.lastUpdated.toFormattedDateSafely
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

@Composable
private fun ModuleInfoListItem(
    @StringRes title: Int,
    desc: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    infoCanDiffer: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            style = style.copy(color = MaterialTheme.colorScheme.outline),
            modifier = Modifier.weight(1f),
            text = stringResource(id = title) + if (infoCanDiffer) " *" else ""
        )
        Text(
            style = style,
            text = desc
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) = TopAppBar(
    modifier = modifier,
    navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left), contentDescription = null
            )
        }
    },
    actions = actions,
    title = {},
    colors = colors,
    scrollBehavior = scrollBehavior
)