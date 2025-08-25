package com.dergoogler.mmrl.ui.screens.repositories.screens.repository

import android.R.attr.fontWeight
import android.R.attr.host
import android.R.attr.lineHeight
import android.R.attr.maxLines
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.database.entity.Repo
import com.dergoogler.mmrl.ext.fadingEdge
import com.dergoogler.mmrl.ext.isNotNullOrBlank
import com.dergoogler.mmrl.ui.component.Loading
import com.dergoogler.mmrl.ui.component.PageIndicator
import com.dergoogler.mmrl.ui.providable.LocalNavController
import com.dergoogler.mmrl.ext.nullply
import com.dergoogler.mmrl.ext.onClick
import com.dergoogler.mmrl.ext.stripLinks
import com.dergoogler.mmrl.model.ui.TopCategory
import com.dergoogler.mmrl.ui.component.Cover
import com.dergoogler.mmrl.ui.component.LabelItem
import com.dergoogler.mmrl.ui.component.LabelItemDefaults
import com.dergoogler.mmrl.ui.component.TopAppBar
import com.dergoogler.mmrl.ui.component.card.Card
import com.dergoogler.mmrl.ui.component.text.BBCodeText
import com.dergoogler.mmrl.ui.providable.LocalRepo
import com.dergoogler.mmrl.viewmodel.RepositoryViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import dev.dergoogler.mmrl.compat.core.LocalUriHandler

@Destination<RootGraph>()
@Composable
fun RepositoryScreen(repo: Repo) {
    val viewModel = RepositoryViewModel.build(repo)
    val list by viewModel.online.collectAsStateWithLifecycle()

    val browser = LocalUriHandler.current
    val density = LocalDensity.current
    val navController = LocalNavController.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()

    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::closeSearch
    )

    val domainRegex = Regex(
        """\b((?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,})\b"""
    )

    val modules = remember(list) { list.map { it.second } }
    val topCategories = remember(modules) { TopCategory.fromModuleList(modules) }

    var coverHeight by remember { mutableIntStateOf(0) }
    var cardHeight by remember { mutableIntStateOf(0) }

    CompositionLocalProvider(
        LocalRepo provides repo
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box {
                    Cover(
                        modifier = Modifier
                            .fadingEdge(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = Float.POSITIVE_INFINITY,
                                    endY = 0f
                                )
                            )
                            .onGloballyPositioned { coordinates ->
                                coverHeight = coordinates.size.height
                            },
                        url = repo.cover ?: "ERROR ME",
                        errorIcon = R.drawable.box,
                    )

                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_left),
                                    contentDescription = null
                                )
                            }
                        },
                        title = {},
                        colors = TopAppBarDefaults.topAppBarColors().copy(
                            scrolledContainerColor = MaterialTheme.colorScheme.background,
                            containerColor = Color.Transparent
                        ),
                        scrollBehavior = scrollBehavior
                    )

                    Box(
                        modifier = Modifier
                            .offset(y = 164.4.dp)
                            .align(Alignment.TopCenter)
                            .fillMaxWidth(0.85f)
                            .onGloballyPositioned { coordinates ->
                                cardHeight = coordinates.size.height
                            }
                    ) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                            MaterialTheme.colorScheme.background
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(
                                            Float.POSITIVE_INFINITY,
                                            Float.POSITIVE_INFINITY
                                        )
                                    ),
                                    RoundedCornerShape(20.dp)
                                )
                                .border(
                                    Dp.Hairline,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(20.dp)
                                ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .relative()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = repo.name,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )

                                BBCodeText(
                                    text = (repo.description
                                        ?: stringResource(R.string.view_module_no_description)).stripLinks(
                                        "<URL>"
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    lineHeight = 20.sp
                                )

                                FlowRow {
                                    repo.nullply {
                                        if (submission.isNotNullOrBlank()) {
                                            LabelItem(
                                                style = LabelItemDefaults.style.copy(
                                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                modifier = Modifier.onClick {
                                                    browser.openUri(submission)
                                                },
                                                icon = getDomainIcon(submission),
                                                text = stringResource(R.string.repo_options_submission)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    with(density) {
                        Spacer(modifier = Modifier.height(((coverHeight / 1.4f) + cardHeight).toDp()))
                    }
                }
            }


            if (viewModel.isLoading) {
                item { Loading() }
                return@LazyColumn
            }

            if (list.isEmpty() && !viewModel.isLoading) {
                item {
                    PageIndicator(
                        icon = R.drawable.cloud,
                        text = if (viewModel.isSearch) R.string.search_empty else R.string.repository_empty,
                    )
                }
                return@LazyColumn
            }

            item {
                TopPicks(
                    label = stringResource(R.string.page_modules),
                    list = modules.take(9)
                )
            }

            items(
                items = topCategories,
                key = { it.label }
            ) {
                TopPicks(
                    label = it.label,
                    list = it.modules
                )
            }

            item {
                Spacer(
                    Modifier.height(16.dp)
                )
            }
        }
    }
}

@Composable
fun getDomainIcon(url: String) = when (getDomain(url)) {
    "github.com" -> R.drawable.github
    else -> R.drawable.link
}

fun getDomain(url: String): String {
    return try {
        val uri = url.toUri()
        val host = uri.host ?: ""
        // Remove "www." if present
        if (host.startsWith("www.")) host.substring(4) else host
    } catch (e: Exception) {
        ""
    }
}