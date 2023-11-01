package ru.nsk.samplephotogallery.ui.fullphoto

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import ru.nsk.samplephotogallery.R

@Composable
fun FullPhotoComposeView(
    photoUri: Uri,
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: IFullPhotoViewModel = FullPhotoViewModel(LocalContext.current, photoUri),
) {
    val state by viewModel.model.stateFlow.collectAsStateWithLifecycle()
    Photo(
        photoUri = state.photoUri,
        modifier = modifier.fillMaxSize(),
    ) {
        navController.navigateUp()
    }
}

@Composable
private fun Photo(photoUri: Uri, modifier: Modifier = Modifier, onVerticallyDragged: () -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(model = photoUri),
        contentDescription = stringResource(R.string.full_photo),
        modifier = modifier,
    )
}