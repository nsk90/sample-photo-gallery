package ru.nsk.samplephotogallery.ui.main

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.nsk.samplephotogallery.R
import ru.nsk.samplephotogallery.ui.theme.SamplePhotoGalleryTheme

@Composable
fun MainComposableView(viewModel: MainViewModel = MainViewModel()) {
    val state = viewModel.model.stateFlow.collectAsState()
    Column {
        Text(
            text = stringResource(R.string.sample_photo_gallery),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(CenterHorizontally)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = stringResource(R.string.preview),
            modifier = Modifier.align(CenterHorizontally),
        )
        TakePictureButton(
            viewModel = viewModel,
            modifier = Modifier.align(CenterHorizontally),
        )
    }
}

@Composable
fun TakePictureButton(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Button(
        onClick = { viewModel.takePicture() },
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.take_picture)
        )
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
            MainComposableView()
        }
    }
}