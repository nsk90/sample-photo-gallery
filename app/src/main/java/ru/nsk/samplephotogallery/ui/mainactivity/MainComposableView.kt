package ru.nsk.samplephotogallery.ui.mainactivity

import android.content.res.Configuration
import android.graphics.Color.GREEN
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.GlobalScope
import ru.nsk.samplephotogallery.R
import ru.nsk.samplephotogallery.ui.theme.SamplePhotoGalleryTheme

@Composable
fun MainComposableView(viewModel: IMainViewModel, modifier: Modifier = Modifier, askPermissions: Boolean = true) {
    val state by viewModel.model.stateFlow.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.sample_photo_gallery),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(CenterHorizontally)
                .wrapContentSize()
        )
        if (askPermissions)
            CameraPermission(modifier = Modifier.align(CenterHorizontally))
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            CameraPreview(Modifier.matchParentSize()) {
                viewModel.onPreviewInflated(it)
            }
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.preview),
                modifier = Modifier
                    .offset((20).dp, (-20).dp)
                    .size(Dp(100F))
                    .clickable { viewModel.onPreviewClicked() }
            )
        }
        TakePictureButton(
            modifier = Modifier
                .align(CenterHorizontally)
                .wrapContentSize()
        ) {
            viewModel.takePicture()
        }
    }
}

@Composable
fun TakePictureButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.take_picture)
        )
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier, onPreviewInflated: (PreviewView) -> Unit) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                setBackgroundColor(GREEN)
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FILL_CENTER
                onPreviewInflated(this)
            }
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CameraPermission(modifier: Modifier = Modifier) {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA,
    )

    if (cameraPermissionState.status.isGranted) {
        Text(stringResource(R.string.camera_permission_granted), modifier)
    } else {
        Column(modifier) {
            val text = if (cameraPermissionState.status.shouldShowRationale) {
                stringResource(R.string.please_grant_camera_permission_rationale)
            } else {
                stringResource(R.string.camera_permission_required)
            }
            Text(text)
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text(stringResource(R.string.request_camera_permission))
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun GreetingPreview() {
    SamplePhotoGalleryTheme {
        Surface(modifier = Modifier/*.fillMaxSize()*/, color = MaterialTheme.colorScheme.background) {
            MainComposableView(
                object : IMainViewModel {
                    override val model = model(GlobalScope, MainState(42))
                    override fun takePicture() = Unit
                    override fun onPreviewInflated(view: PreviewView) = Unit
                    override fun onPreviewClicked() = Unit
                },
                askPermissions = false,
            )
        }
    }
}