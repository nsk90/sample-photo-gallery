package ru.nsk.samplephotogallery.ui.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.GlobalScope
import ru.nsk.samplephotogallery.application.thisApplication
import ru.nsk.samplephotogallery.domain.Photo
import ru.nsk.samplephotogallery.ui.mainactivity.Deeplink

private val itemSize = 120.dp

@Composable
fun GalleryComposeView(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: IGalleryViewModel = viewModel(
        factory = GalleryViewModelFactory(
            LocalContext.current,
            LocalContext.current.thisApplication.photoStorage
        )
    ),
) {
    val state by viewModel.model.stateFlow.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = itemSize),
        modifier = modifier,
    ) {
        itemsIndexed(state.photos) { index, photo ->
            PhotoItem(photo) { navController.navigate("${Deeplink.FULL_PHOTO.path}/$index") }
        }
    }
}

@Composable
private fun PhotoItem(photo: Photo, modifier: Modifier = Modifier, onClick: (Photo) -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(model = photo.uri),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .fillMaxSize()
            .size(itemSize)
            .clickable(onClick = { onClick(photo) })
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