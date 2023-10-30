package ru.nsk.samplephotogallery.ui.mainactivity

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.tools.log.log
import ru.nsk.samplephotogallery.tools.log.toast
import ru.nsk.samplephotogallery.tools.log.toastException

data class MainState(val preview: Int)

interface IMainViewModel : MviModelHost<MainState> {
    fun takePicture()
    fun onPreviewInflated(view: PreviewView)
    fun onPreviewClicked()
}

class MainViewModel(context: Context) : IMainViewModel, ViewModel(), DefaultLifecycleObserver {
    override val model = model(viewModelScope, MainState(preview = 42))

    @SuppressLint("StaticFieldLeak")
    private val context = context.applicationContext
    private val cameraController = LifecycleCameraController(context.applicationContext)

    init {
        log { "init" }
        ActivityResultContracts.RequestPermission()
    }

    override fun onCreate(owner: LifecycleOwner) {
        cameraController.apply {
            bindToLifecycle(owner)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            initializationFuture.addListener({
                this@apply.toast(context) { "initializationFuture complete" }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    override fun takePicture() = intent {
        cameraController.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    toast(context) { "Photo capture succeeded: $image" }
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

    override fun onPreviewClicked() {
        // todo
    }
}