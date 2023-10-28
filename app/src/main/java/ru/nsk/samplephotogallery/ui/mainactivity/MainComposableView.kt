package ru.nsk.samplephotogallery.ui.mainactivity

import android.content.res.Configuration
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import ru.nsk.samplephotogallery.R
import ru.nsk.samplephotogallery.architecture.mvi.MviModel
import ru.nsk.samplephotogallery.ui.theme.SamplePhotoGalleryTheme

@Composable
fun MainComposableView(viewModel: IMainViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.model.stateFlow.collectAsState()
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.sample_photo_gallery),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(CenterHorizontally)
        )
        CameraPermission(modifier = Modifier.align(CenterHorizontally))
        CameraPreview(
            viewModel = viewModel,
            Modifier
                .align(CenterHorizontally)
                .fillMaxWidth()
                .height(Dp(300F))
        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = stringResource(R.string.preview),
            modifier = Modifier
                .align(CenterHorizontally)
                .size(Dp(150F)),
        )
        TakePictureButton(
            viewModel = viewModel,
            modifier = Modifier.align(CenterHorizontally),
        )
    }
}

@Composable
fun TakePictureButton(viewModel: IMainViewModel, modifier: Modifier = Modifier) {
    Button(
        onClick = { viewModel.takePicture() },
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.take_picture)
        )
    }
}

@Composable
fun CameraPreview(viewModel: IMainViewModel, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                setBackgroundColor(Color.GREEN)
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FIT_CENTER
                controller = viewModel.cameraController
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
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
fun GreetingPreview() {
    SamplePhotoGalleryTheme {
        Surface(modifier = Modifier/*.fillMaxSize()*/, color = MaterialTheme.colorScheme.background) {
            MainComposableView(object : IMainViewModel {
                override val model: MviModel<MainState>
                    get() = TODO("Not yet implemented")
                override val cameraController: CameraController
                    get() = TODO("Not yet implemented")

                override fun takePicture() {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}