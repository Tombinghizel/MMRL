package com.dergoogler.mmrl.ui.screens.repositories.screens.repository.modules

import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.database.entity.Repo
import com.dergoogler.mmrl.datastore.model.RepositoryMenu
import com.dergoogler.mmrl.ui.component.PageIndicator
import com.dergoogler.mmrl.ui.component.SearchTopBar
import com.dergoogler.mmrl.ext.none
import com.dergoogler.mmrl.ui.component.NavigateUpTopBar
import com.dergoogler.mmrl.ui.component.scaffold.Scaffold
import com.dergoogler.mmrl.ui.component.toolbar.ToolbarTitle
import com.dergoogler.mmrl.ui.providable.LocalDestinationsNavigator
import com.dergoogler.mmrl.ui.remember.rememberOnlineModules
import com.dergoogler.mmrl.ui.remember.rememberUserPreferencesRepository
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun TypedModulesScreen(
    searchKey: String,
    repo: Repo,
    disableSearch: Boolean = false,
) {
    var query by remember { mutableStateOf(searchKey) }
    val modules by rememberOnlineModules(repo, query)
    val userPreferencesRepository = rememberUserPreferencesRepository()
    val scope = rememberCoroutineScope()
    var isSearch by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val openSearch = fun() {
        isSearch = true
    }

    val closeSearch = fun() {
        isSearch = false
        query = ""
    }

    val search = fun(data: String) {
        query = data
    }

    val setRepositoryMenu = fun(value: RepositoryMenu) {
        scope.launch {
            userPreferencesRepository.setRepositoryMenu(value)
        }
    }

    BackHandler(
        enabled = isSearch,
        onBack = closeSearch
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                disableSearch = disableSearch,
                repo = repo,
                isSearch = isSearch,
                query = query,
                onQueryChange = search,
                onOpenSearch = openSearch,
                onCloseSearch = closeSearch,
                setMenu = setRepositoryMenu,
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (modules.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.cloud,
                    text = if (isSearch) R.string.search_empty else R.string.repository_empty,
                )
            }

            this@Scaffold.TypedModulesList(
                repo = repo,
                list = modules,
                state = listState,
            )
        }
    }
}

@Composable
private fun TopBar(
    repo: Repo,
    isSearch: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    setMenu: (RepositoryMenu) -> Unit,
    disableSearch: Boolean,
) {
    val navigator = LocalDestinationsNavigator.current

    var currentQuery by remember { mutableStateOf(query) }
    DisposableEffect(isSearch) {
        onDispose { currentQuery = "" }
    }

    if (disableSearch) {
        val title = remember {
            query.replace(Regex("^((author|category|id):)(.+)", RegexOption.IGNORE_CASE), "$3")
        }

        NavigateUpTopBar(
            title = title,
            subtitle = repo.name,
            navigator = navigator
        )

        return
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
        },
        title = {
            ToolbarTitle(
                title = repo.name
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