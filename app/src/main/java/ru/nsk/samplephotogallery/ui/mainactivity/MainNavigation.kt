package ru.nsk.samplephotogallery.ui.mainactivity

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.nsk.samplephotogallery.ui.fullphoto.FullPhotoComposeView
import ru.nsk.samplephotogallery.ui.gallery.GalleryComposeView

enum class Deeplink {
    MAIN,
    GALLERY,
    FULL_PHOTO,
}

@Composable
fun MainNavigation(mainViewModel: MainViewModel, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Deeplink.MAIN.name) {
        composable(Deeplink.MAIN.name) { MainComposableView(navController = navController, viewModel = mainViewModel) }
        composable(Deeplink.GALLERY.name) { GalleryComposeView(navController = navController) }
        composable(Deeplink.FULL_PHOTO.name) { FullPhotoComposeView(navController = navController) }
    }
}
