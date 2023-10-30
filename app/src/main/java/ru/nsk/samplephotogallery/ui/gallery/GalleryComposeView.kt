package ru.nsk.samplephotogallery.ui.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.GlobalScope

@Composable
fun GalleryComposeView(viewModel: IGalleryViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.model.stateFlow.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = modifier,
    ) {
        items(state.photos) { photo ->
            PhotoItem(photo) { viewModel.onPhotoClicked(photo) }
        }
    }
}

@Composable
fun PhotoItem(photo: Photo, onClick: () -> Unit) {
    Image(
        painter = BitmapPainter(image = ImageBitmap(10, 10)),//fixme ?
        contentDescription = photo.id,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Preview
@Composable
fun GalleryComposePreview() {
    GalleryComposeView(
        object : IGalleryViewModel {
            override val model = model(GlobalScope, GalleryState(emptyList()))
            override fun onPhotoClicked(photo: Photo) = Unit
        }
    )
}