package ru.nsk.samplephotogallery.ui.fullphoto

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import ru.nsk.samplephotogallery.R

@Composable
fun FullPhotoComposeView(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: IFullPhotoViewModel = FullPhotoViewModel(LocalContext.current),
) {
    val state by viewModel.model.stateFlow.collectAsStateWithLifecycle()
    Photo(state.photoUri) {}
}

@Composable
private fun Photo(photoUri: Uri, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(model = photoUri),
        contentDescription = stringResource(R.string.full_photo),
        modifier = modifier
            .clickable(onClick = onClick)
            .size(50.dp)
    )
}