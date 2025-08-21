package com.dergoogler.mmrl.ui.screens.repositories.screens.repository

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.datastore.model.RepoListMode
import com.dergoogler.mmrl.ext.navigateSingleTopTo
import com.dergoogler.mmrl.ext.panicString
import com.dergoogler.mmrl.model.online.OnlineModule
import com.dergoogler.mmrl.model.state.OnlineState
import com.dergoogler.mmrl.ui.component.listItem.dsl.List
import com.dergoogler.mmrl.ui.component.listItem.dsl.ListItemSlot
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.ButtonItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Icon
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.ui.component.scaffold.ScaffoldScope
import com.dergoogler.mmrl.ui.component.scrollbar.VerticalFastScrollbar
import com.dergoogler.mmrl.ui.navigation.graphs.RepositoriesScreen
import com.dergoogler.mmrl.ui.providable.LocalModule
import com.dergoogler.mmrl.ui.providable.LocalModuleState
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
                        list = list
                    )
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
