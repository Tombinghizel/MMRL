package com.dergoogler.mmrl.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.dergoogler.mmrl.ext.panicArguments
import com.dergoogler.mmrl.ui.navigation.BottomNavRoute
import com.dergoogler.mmrl.ui.providable.LocalNavController
import com.dergoogler.mmrl.ui.providable.LocalPanicArguments
import com.dergoogler.mmrl.ui.screens.repositories.screens.exploreRepositories.ExploreRepositoriesScreen
import com.dergoogler.mmrl.ui.screens.repositories.screens.exploreRepositories.ExploreRepositoryScreen
import com.dergoogler.mmrl.ui.screens.repositories.screens.main.RepositoriesScreen
import com.dergoogler.mmrl.ui.screens.repositories.screens.repository.RepositoryScreen
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.FilteredSearchScreen
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.NewViewScreen
import com.dergoogler.mmrl.ui.screens.repositories.screens.view.ViewDescriptionScreen
import com.dergoogler.mmrl.viewmodel.ModuleViewModel
import com.dergoogler.mmrl.viewmodel.RepositoriesViewModel
import com.dergoogler.mmrl.viewmodel.RepositoryViewModel

enum class RepositoriesScreen(val route: String) {
    Home("Repository"),
    View("View/{moduleId}/{repoUrl}"),
    RepositoryView("RepositoryView/{repoUrl}/{repoName}"),
    Description("Description/{moduleId}/{repoUrl}"),
    RepoSearch("RepoSearch/{repoUrl}/{type}/{value}"),
    ExploreRepositories("ExploreRepositories"),
    ExploreRepository("ExploreRepository/{repo}")
}

fun NavGraphBuilder.repositoryScreen() = navigation(
    startDestination = RepositoriesScreen.Home.route,
    route = BottomNavRoute.Repository.route
) {
    composable(
        route = RepositoriesScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        val viewModel = hiltViewModel<RepositoriesViewModel>()

        RepositoriesScreen(
            viewModel = viewModel
        )
    }

    composable(
        route = RepositoriesScreen.RepositoryView.route,
        arguments = listOf(
            navArgument("repoUrl") { type = NavType.StringType },
            navArgument("repoName") { type = NavType.StringType }
        ),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        val arguments = it.panicArguments

        val repositoryViewModel =
            hiltViewModel<RepositoryViewModel, RepositoryViewModel.Factory> { factory ->
                factory.create(arguments)
            }

        CompositionLocalProvider(
            LocalPanicArguments provides arguments
        ) {
            RepositoryScreen(
                viewModel = repositoryViewModel
            )
        }
    }

    composable(
        route = RepositoriesScreen.ExploreRepositories.route,
        enterTransition = { scaleIn() + fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        ExploreRepositoriesScreen()
    }

    composable(
        route = RepositoriesScreen.ExploreRepository.route,
        arguments = listOf(
            navArgument("repo") { type = NavType.StringType },
        ),
        enterTransition = { scaleIn() + fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        val arguments = it.panicArguments

        CompositionLocalProvider(
            LocalPanicArguments provides arguments
        ) {
            ExploreRepositoryScreen()
        }
    }

    moduleScreen()
}

fun NavGraphBuilder.moduleScreen(
    navController: ProvidableCompositionLocal<NavHostController> = LocalNavController,
) {
    composable(
        route = RepositoriesScreen.View.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("repoUrl") { type = NavType.StringType },
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://mmrl.dergoogler.com/module/{repoUrl}/{moduleId}"
            },
            navDeepLink {
                uriPattern = "http://mmrl.dergoogler.com/module/{repoUrl}/{moduleId}"
            }
        ),
        enterTransition = { scaleIn() + fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        val arguments = it.panicArguments

        val moduleViewModel = hiltViewModel<ModuleViewModel, ModuleViewModel.Factory> { factory ->
            factory.create(arguments)
        }

        CompositionLocalProvider(
            LocalPanicArguments provides arguments,
        ) {
            NewViewScreen(
                navController = navController.current,
                viewModel = moduleViewModel,
            )
        }
    }

    composable(
        route = RepositoriesScreen.Description.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("repoUrl") { type = NavType.StringType },
        ),
        enterTransition = { scaleIn() + fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        val arguments = it.panicArguments

        val moduleViewModel = hiltViewModel<ModuleViewModel, ModuleViewModel.Factory> { factory ->
            factory.create(arguments)
        }

        CompositionLocalProvider(
            LocalPanicArguments provides arguments
        ) {
            ViewDescriptionScreen(
                navController = navController.current,
                viewModel = moduleViewModel
            )
        }
    }

    composable(
        route = RepositoriesScreen.RepoSearch.route,
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
            navArgument("repoUrl") { type = NavType.StringType },
            navArgument("value") { type = NavType.StringType }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://mmrl.dergoogler.com/search/{repoUrl}/{type}/{value}"
            },
            navDeepLink {
                uriPattern = "http://mmrl.dergoogler.com/search/{repoUrl}/{type}/{value}"
            }
        ),
        enterTransition = { scaleIn() + fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        val arguments = it.panicArguments

        val repositoryViewModel =
            hiltViewModel<RepositoryViewModel, RepositoryViewModel.Factory> { factory ->
                factory.create(arguments)
            }

        CompositionLocalProvider(
            LocalPanicArguments provides arguments
        ) {
            FilteredSearchScreen(
                navController = navController.current,
                viewModel = repositoryViewModel
            )
        }
    }
}