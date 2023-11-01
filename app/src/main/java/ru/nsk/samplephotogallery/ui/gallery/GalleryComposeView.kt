package ru.nsk.samplephotogallery.ui.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.GlobalScope
import ru.nsk.samplephotogallery.ui.mainactivity.Deeplink

@Composable
fun GalleryComposeView(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: IGalleryViewModel = GalleryViewModel(LocalContext.current),
) {
    val state by viewModel.model.stateFlow.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = modifier,
    ) {
        items(state.photos) { photo ->
            PhotoItem(photo) { navController.navigate(Deeplink.FULL_PHOTO.name) }
        }
    }
}

@Composable
private fun PhotoItem(photo: Photo, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(model = photo.file.uri),
        contentDescription = photo.file.id.toString(),
        modifier = modifier
            .clickable(onClick = onClick)
            .size(50.dp)
    )
}

@Preview
@Composable
private fun GalleryComposePreview() {
    GalleryComposeView(
        viewModel = object : IGalleryViewModel {
            override val model = model(GlobalScope, GalleryState(emptyList()))
        }
    )
}