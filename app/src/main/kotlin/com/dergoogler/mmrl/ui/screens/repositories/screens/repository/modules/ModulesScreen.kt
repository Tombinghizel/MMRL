package com.dergoogler.mmrl.ui.screens.repositories.screens.repository.modules

import androidx.compose.runtime.Composable
import com.dergoogler.mmrl.ui.screens.repositories.screens.repository.ModulesList
import com.dergoogler.mmrl.ui.screens.repositories.screens.repository.RepositoryMenu
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.datastore.model.RepositoryMenu
import com.dergoogler.mmrl.ui.component.Loading
import com.dergoogler.mmrl.ui.component.PageIndicator
import com.dergoogler.mmrl.ui.component.SearchTopBar
import com.dergoogler.mmrl.ui.providable.LocalNavController
import com.dergoogler.mmrl.ui.providable.LocalPanicArguments
import com.dergoogler.mmrl.ext.none
import com.dergoogler.mmrl.ext.panicString
import com.dergoogler.mmrl.ui.component.scaffold.Scaffold
import com.dergoogler.mmrl.ui.component.toolbar.ToolbarTitle
import com.dergoogler.mmrl.viewmodel.RepositoryViewModel

@Composable
fun ModulesScreen(
    viewModel: RepositoryViewModel,
) {
    val list by viewModel.online.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    val navController = LocalNavController.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::closeSearch
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                query = query,
                onQueryChange = viewModel::search,
                onOpenSearch = viewModel::openSearch,
                onCloseSearch = viewModel::closeSearch,
                setMenu = viewModel::setRepositoryMenu,
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (viewModel.isLoading) {
                Loading()
            }

            if (list.isEmpty() && !viewModel.isLoading) {
                PageIndicator(
                    icon = R.drawable.cloud,
                    text = if (viewModel.isSearch) R.string.search_empty else R.string.repository_empty,
                )
            }

            this@Scaffold.ModulesList(
                list = list,
                state = listState,
                navController = navController
            )
        }
    }
}

@Composable
private fun TopBar(
    isSearch: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    setMenu: (RepositoryMenu) -> Unit,
) {
    val arguments = LocalPanicArguments.current

    var currentQuery by remember { mutableStateOf(query) }
    DisposableEffect(isSearch) {
        onDispose { currentQuery = "" }
    }

    SearchTopBar(
        isSearch = isSearch,
        query = currentQuery,
        onQueryChange = {
            onQueryChange(it)
            currentQuery = it
        },
        onClose = {
            onCloseSearch()
            currentQuery = ""
        }, title = {
            ToolbarTitle(
                title = arguments.panicString("repoName")
            )
        },
        actions = {
            if (!isSearch) {
                IconButton(
                    onClick = onOpenSearch
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = null
                    )
                }
            }

            RepositoryMenu(
                setMenu = setMenu
            )
        }
    )
}