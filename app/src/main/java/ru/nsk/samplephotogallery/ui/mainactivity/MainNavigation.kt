package ru.nsk.samplephotogallery.ui.mainactivity

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.nsk.samplephotogallery.ui.fullphoto.FullPhotoComposeView
import ru.nsk.samplephotogallery.ui.gallery.GalleryComposeView

private const val INITIAL_PHOTO_INDEX_ARG = "initialPhotoIndex"

enum class Deeplink(val route: String, val path: String = route) {
    MAIN("main"),
    GALLERY("gallery"),
    FULL_PHOTO("fullphoto/{$INITIAL_PHOTO_INDEX_ARG}", "fullphoto"), // fixme use typesafe navigation
}

@Composable
fun MainNavigation(mainViewModel: MainViewModel, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Deeplink.MAIN.route) {
        composable(Deeplink.MAIN.route) { MainComposableView(navController = navController, viewModel = mainViewModel) }
        composable(Deeplink.GALLERY.route) { GalleryComposeView(navController = navController) }
        composable(
            Deeplink.FULL_PHOTO.route,
            arguments = listOf(
                navArgument(INITIAL_PHOTO_INDEX_ARG) { type = NavType.IntType },
            )
        ) {
            FullPhotoComposeView(
                initialPhotoIndex = it.arguments!!.getInt(INITIAL_PHOTO_INDEX_ARG),
                navController = navController,
            )
        }
    }
}
