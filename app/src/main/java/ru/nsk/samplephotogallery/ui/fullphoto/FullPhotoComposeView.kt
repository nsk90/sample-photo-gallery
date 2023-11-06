package ru.nsk.samplephotogallery.ui.fullphoto

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import ru.nsk.samplephotogallery.R
import ru.nsk.samplephotogallery.application.thisApplication
import ru.nsk.samplephotogallery.tools.log.log
import kotlin.math.abs
import kotlin.math.roundToInt

private const val INITIAL_SCALE = 1f
private const val INITIAL_TAPPED_SCALE = 1.7f

private enum class DragValue { Start, Center, End }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullPhotoComposeView(
    initialPhotoIndex: Int,
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: IFullPhotoViewModel = viewModel(
        factory = FullPhotoViewModelFactory(
            LocalContext.current,
            LocalContext.current.thisApplication.photoStorage,
            initialPhotoIndex
        )
    ),
) {
    val state by viewModel.model.stateFlow.collectAsStateWithLifecycle()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart
    ) {
        val pagerState = rememberPagerState(initialPage = state.initialIndex) { state.photos.size }

        var indexState by remember {
            mutableIntStateOf(state.initialIndex)
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically,
        ) { index ->
            1.log { "index $index "}
            indexState = index
            Photo(
                photoUri = state.photos[index].uri,
                modifier = Modifier.fillMaxSize(),
                onVerticallyDragged = {
                    navController.navigateUp()
                },
            )
        }
        Column {
            TextButton(stringResource(R.string.view_in_gallery_app)) { viewModel.viewInGalleryApp(indexState) }
            TextButton(stringResource(R.string.save_to_album)) { viewModel.saveToAlbum(indexState) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Photo(
    photoUri: Uri,
    modifier: Modifier = Modifier,
    onVerticallyDragged: () -> Unit
) {
    var zoomed by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var scale by remember { mutableFloatStateOf(INITIAL_SCALE) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState(
        onTransformation = { zoomChange, panChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 3f)
            if (scale == 1f) zoomed = false

            val maxOffsetX = abs((size.width - (size.width * scale)) / 2)
            val maxOffsetY = abs((size.height - (size.height * scale)) / 2)
            offset = Offset(
                (offset.x + panChange.x).coerceIn(-maxOffsetX, maxOffsetX),
                (offset.y + panChange.y).coerceIn(-maxOffsetY, maxOffsetY)
            )
        }
    )
    val density = LocalDensity.current
    val anchors = remember {
        with(density) {
            DraggableAnchors {
                DragValue.Start at -200.dp.toPx()
                DragValue.Center at 0f
                DragValue.End at 200.dp.toPx()
            }
        }
    }

    var wasDragged by remember { mutableStateOf(false) }
    val draggableVerticalState = remember {
        AnchoredDraggableState(
            DragValue.Center,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.8f },
            velocityThreshold = { Float.MAX_VALUE },
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        ) {
            if (it in listOf(DragValue.Start, DragValue.End) && !wasDragged) {
                wasDragged = true
                onVerticallyDragged()
            }
            true
        }
    }

    Image(
        painter = rememberAsyncImagePainter(model = photoUri),
        contentDescription = stringResource(R.string.full_photo),
        modifier = modifier
            .onSizeChanged { size = it }
            .transformable(transformableState, enabled = zoomed)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        zoomed = !zoomed
                        scale = if (zoomed) {
                            INITIAL_TAPPED_SCALE
                        } else {
                            offset = Offset.Zero
                            INITIAL_SCALE
                        }
                    }
                )
            }
            .anchoredDraggable(draggableVerticalState, Vertical, enabled = scale == INITIAL_SCALE)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
            )
            .offset {
                IntOffset(
                    x = 0,
                    y = draggableVerticalState
                        .requireOffset()
                        .roundToInt(),
                )
            }
    )
}

@Composable
private fun TextButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(text = text)
    }
}