package ru.nsk.samplephotogallery.ui.mainactivity

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.nsk.samplephotogallery.ui.gallery.GalleryComposeView

enum class Deeplink(val value: String) {
    MAIN("main"),
    GALLERY("gallery"),
}

@Composable
fun MainNavigation(mainViewModel: MainViewModel, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Deeplink.MAIN.value) {
        composable(Deeplink.MAIN.value) { MainComposableView(navController, mainViewModel) }
        composable(Deeplink.GALLERY.value) { GalleryComposeView() }
    }
}
