package com.dergoogler.mmrl.ui.screens.repositories.screens.repository

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dergoogler.mmrl.model.online.OnlineModule
import com.dergoogler.mmrl.model.state.OnlineState
import com.dergoogler.mmrl.model.ui.TopCategory
import com.dergoogler.mmrl.ui.component.scaffold.ScaffoldScope
import com.dergoogler.mmrl.ui.component.scrollbar.VerticalFastScrollbar
import com.dergoogler.mmrl.ui.providable.LocalPanicArguments
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences

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

    val modules = remember(list) { list.map { it.second } }
    val topCategories = remember(modules) { TopCategory.fromModuleList(modules) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        this@ModulesList.ResponsiveContent {
            LazyColumn(
                state = state,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                before?.let {
                    item {
                        it()
                    }
                }

                item {
                    TopPicks(
                        label = "Modules",
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

                after?.let {
                    item {
                        it()
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        VerticalFastScrollbar(
            state = state,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}
