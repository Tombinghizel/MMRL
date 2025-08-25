package com.dergoogler.mmrl.ui.screens.repositories.screens.repository.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dergoogler.mmrl.datastore.model.RepoListMode
import com.dergoogler.mmrl.ext.navigateSingleTopTo
import com.dergoogler.mmrl.ext.panicString
import com.dergoogler.mmrl.model.online.OnlineModule
import com.dergoogler.mmrl.model.state.OnlineState
import com.dergoogler.mmrl.ui.component.scaffold.ScaffoldScope
import com.dergoogler.mmrl.ui.component.scrollbar.VerticalFastScrollbar
import com.dergoogler.mmrl.ui.providable.LocalModule
import com.dergoogler.mmrl.ui.providable.LocalModuleState
import com.dergoogler.mmrl.ui.providable.LocalPanicArguments
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ui.screens.repositories.screens.main.RepositoriesScreen
import com.dergoogler.mmrl.ui.screens.repositories.screens.repository.ModuleItemCompact
import com.dergoogler.mmrl.ui.screens.repositories.screens.repository.ModuleItemDetailed

@Composable
fun ScaffoldScope.ModulesList(
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    list: List<Pair<OnlineState, OnlineModule>>,
    state: LazyListState,
    navController: NavController,
) {
    val userPreferences = LocalUserPreferences.current
    val menu = userPreferences.repositoryMenu
    val arguments = LocalPanicArguments.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        this@ModulesList.ResponsiveContent {
            LazyColumn(
                state = state,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(if (menu.repoListMode == RepoListMode.Compact) 0.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                before?.let {
                    item {
                        it()
                    }
                }

                items(
                    items = list,
                    key = { it.second.id }
                ) { (moduleState, module) ->
                    CompositionLocalProvider(
                        LocalModuleState provides moduleState,
                        LocalModule provides module
                    ) {
                        val click = {
//                            navController.navigateSingleTopTo(
//                                route = RepositoriesScreen.View.route,
//                                args = mapOf(
//                                    "moduleId" to module.id,
//                                    "repoUrl" to arguments.panicString("repoUrl")
//                                )
//                            )
                        }

                        when (menu.repoListMode) {
                            RepoListMode.Compact -> ModuleItemCompact(
                                onClick = click
                            )

                            RepoListMode.Detailed -> ModuleItemDetailed(
                                onClick = click
                            )
                        }
                    }
                }

                after?.let {
                    item {
                        it()
                    }
                }
            }
        }

        VerticalFastScrollbar(
            state = state,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}
