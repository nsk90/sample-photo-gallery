package ru.nsk.samplephotogallery.ui.fullphoto

import android.net.Uri
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import ru.nsk.samplephotogallery.R
import kotlin.math.roundToInt

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
        onHorizontallyDragged = { /*todo*/},
        onVerticallyDragged = { navController.navigateUp() },
    )
}

private const val INITIAL_SCALE = 1f

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Photo(
    photoUri: Uri,
    modifier: Modifier = Modifier,
    onHorizontallyDragged: () -> Unit,
    onVerticallyDragged: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(INITIAL_SCALE) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState(
        onTransformation = { zoomChange, offsetChange, _ ->
            scale = maxOf(scale * zoomChange, 1f)
            offset += offsetChange
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

    val draggableHorizontalState = remember {
        AnchoredDraggableState(
            DragValue.Center,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 300.dp.toPx() } },
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        )
    }
    val draggableVerticalState = remember {
        AnchoredDraggableState(
            DragValue.Center,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.8f },
            velocityThreshold = { Float.MAX_VALUE },
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        )
    }
    if (draggableHorizontalState.currentValue in listOf(DragValue.Start, DragValue.End))
        onHorizontallyDragged()
    if (draggableVerticalState.currentValue in listOf(DragValue.Start, DragValue.End))
        onVerticallyDragged()

    Image(
        painter = rememberAsyncImagePainter(model = photoUri),
        contentDescription = stringResource(R.string.full_photo),
        modifier = modifier
            .transformable(transformableState)
            .anchoredDraggable(draggableHorizontalState, Horizontal, enabled = scale == INITIAL_SCALE)
            .anchoredDraggable(draggableVerticalState, Vertical, enabled = scale == INITIAL_SCALE)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .offset {
                IntOffset(
                    x = draggableHorizontalState
                        .requireOffset()
                        .roundToInt(),
                    y = draggableVerticalState
                        .requireOffset()
                        .roundToInt(),
                )
            }
        )
}

enum class DragValue { Start, Center, End }