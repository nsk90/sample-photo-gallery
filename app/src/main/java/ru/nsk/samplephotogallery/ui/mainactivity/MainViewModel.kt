package ru.nsk.samplephotogallery.ui.mainactivity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images
import android.provider.MediaStore.MediaColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.domain.MediaStoreUtils
import ru.nsk.samplephotogallery.tools.log.toast
import ru.nsk.samplephotogallery.tools.log.toastException

/** TODO Use DI framework instead */
class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(context) as T
    }
}

data class MainState(val preview: Uri)

interface IMainViewModel : MviModelHost<MainState> {
    fun takePicture()
    fun onPreviewInflated(view: PreviewView)
}

class MainViewModel(context: Context) : IMainViewModel, ViewModel(), DefaultLifecycleObserver {
    override val model = model(viewModelScope, MainState(preview = Uri.EMPTY))

    @SuppressLint("StaticFieldLeak")
    private val context = context.applicationContext
    private val cameraController = LifecycleCameraController(context.applicationContext)

    init {
        ActivityResultContracts.RequestPermission()
        intent {
            state {
                // fixme preview should be updated when new photos are made
                copy(preview = Uri.parse(MediaStoreUtils(context).getLatestImageFilename().orEmpty()))
            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        cameraController.apply {
            bindToLifecycle(owner)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            initializationFuture.addListener(
                { this@apply.toast(context) { "initializationFuture complete" } },
                ContextCompat.getMainExecutor(context)
            )
        }
    }

    override fun takePicture() = intent {
        val contentValues = ContentValues()
        contentValues.put(MediaColumns.MIME_TYPE, "image/jpeg")

        cameraController.takePicture(
            ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build(),
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: OutputFileResults) {
                    toast(context) { "Photo capture succeeded: $outputFileResults" }
                }

                override fun onError(exception: ImageCaptureException) {
                    toastException(context, exception) { "Photo capture failed: $exception" }
                }
            }
        )
    }

    override fun onPreviewInflated(view: PreviewView) {
        view.controller = cameraController
    }
}