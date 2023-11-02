package ru.nsk.samplephotogallery.ui.mainactivity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.tools.MediaStoreUtils
import ru.nsk.samplephotogallery.tools.log.log
import ru.nsk.samplephotogallery.tools.log.toast
import ru.nsk.samplephotogallery.tools.log.toastException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                copy(preview = Uri.parse(MediaStoreUtils(context).getLatestImageFilename()))
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
        val storePath = MediaStoreUtils(context).mediaStoreCollection.toString()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss_SSS")
        val name = formatter.format(LocalDateTime.now()) + System.currentTimeMillis().toString()
        log { "storePath $storePath, name: $name" }

        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

        cameraController.takePicture(
            ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
        log { "onPreviewInflated" }
    }
}