package ru.nsk.samplephotogallery.ui.mainactivity

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.nsk.samplephotogallery.ui.fullphoto.FullPhotoComposeView
import ru.nsk.samplephotogallery.ui.gallery.GalleryComposeView

private const val PHOTO_URI_ARG = "photoUri"

enum class Deeplink(val route: String, val path: String = route) {
    MAIN("main"),
    GALLERY("gallery"),
    FULL_PHOTO("fullphoto/{$PHOTO_URI_ARG}", "fullphoto"), // fixme use typesafe navigation
}

@Composable
fun MainNavigation(mainViewModel: MainViewModel, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Deeplink.MAIN.route) {
        composable(Deeplink.MAIN.route) { MainComposableView(navController = navController, viewModel = mainViewModel) }
        composable(Deeplink.GALLERY.route) { GalleryComposeView(navController = navController) }
        composable(
            Deeplink.FULL_PHOTO.route,
            arguments = listOf(navArgument(PHOTO_URI_ARG) { type = NavType.StringType })
        ) {
            FullPhotoComposeView(
                photoUri = Uri.parse(it.arguments!!.getString(PHOTO_URI_ARG)),
                navController = navController
            )
        }
    }
}
