package com.dergoogler.mmrl.ui.screens.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dergoogler.mmrl.model.online.OtherSources
import com.dergoogler.mmrl.ui.providable.LocalDestinationsNavigator
import com.dergoogler.mmrl.ui.providable.LocalHazeState
import com.dergoogler.mmrl.ui.providable.LocalMainScreenInnerPaddings
import com.dergoogler.mmrl.ui.providable.LocalOnlineModule
import com.dergoogler.mmrl.ui.providable.LocalOnlineModuleState
import com.dergoogler.mmrl.ui.screens.repository.ModuleItemCompact
import com.ramcosta.composedestinations.generated.destinations.NewViewScreenDestination
import dev.chrisbanes.haze.hazeSource

@Composable
fun ModulesList(
    innerPadding: PaddingValues,
    list: List<OtherSources>,
    state: LazyListState,
) {
    val navigator = LocalDestinationsNavigator.current

    LazyColumn(
        modifier = Modifier.hazeSource(LocalHazeState.current),
        state = state,
        contentPadding = PaddingValues(
            start = 8.dp,
            top = innerPadding.calculateTopPadding() + 8.dp,
            end = 8.dp,
            bottom = 8.dp
        ),
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
                        navigator.navigate(
                            NewViewScreenDestination(
                                it.repo,
                                it.online
                            )
                        )
                    }
                )
            }
        }
        item {
            val paddingValues = LocalMainScreenInnerPaddings.current
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}