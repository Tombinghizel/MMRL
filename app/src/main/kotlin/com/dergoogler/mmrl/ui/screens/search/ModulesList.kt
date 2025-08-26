package com.dergoogler.mmrl.ui.screens.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.dergoogler.mmrl.model.online.OtherSources
import com.dergoogler.mmrl.ui.providable.LocalMainNavController
import com.dergoogler.mmrl.ui.providable.LocalOnlineModule
import com.dergoogler.mmrl.ui.providable.LocalOnlineModuleState
import com.dergoogler.mmrl.ui.screens.repository.ModuleItemCompact

@Composable
fun ModulesList(
    list: List<OtherSources>,
    state: LazyListState,
) {
    val navController = LocalMainNavController.current

    LazyColumn(
        state = state,
        contentPadding = PaddingValues(8.dp),
    ) {
        items(
            items = list,
            key = { "${it.online.id}_${it.repo.url}_${it.repo.name}" }
        ) {
            CompositionLocalProvider(
                LocalOnlineModuleState provides it.state,
                LocalOnlineModule provides it.online
            ) {
                ModuleItemCompact(
                    sourceProvider = it.repo.name,
                    onClick = {
//                        navController.navigateSingleTopTo(
//                            route = RepositoriesScreen.View.route,
//                            args = mapOf(
//                                "moduleId" to it.online.id,
//                                "repoUrl" to it.repo.url
//                            )
//                        )
                    }
                )
            }
        }
    }
}