package ru.nsk.samplephotogallery.ui.fullphoto

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
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
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState(
        onTransformation = { zoomChange, offsetChange, _ ->
            scale *= zoomChange
            offset += offsetChange
        }
    )
    Image(
        painter = rememberAsyncImagePainter(model = photoUri),
        contentDescription = stringResource(R.string.full_photo),
        modifier = modifier
            .transformable(transformableState)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            ),
    )
}