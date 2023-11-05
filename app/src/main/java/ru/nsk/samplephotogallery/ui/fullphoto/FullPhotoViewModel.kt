package ru.nsk.samplephotogallery.ui.fullphoto

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.tools.MediaStoreUtils
import ru.nsk.samplephotogallery.tools.log.log
import ru.nsk.samplephotogallery.tools.log.toastException

/** TODO Use DI framework instead */
class FullPhotoViewModelFactory(private val context: Context, private val photoUri: Uri) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FullPhotoViewModel(context, photoUri) as T
    }
}

data class FullPhotoState(val photoUri: Uri)
interface IFullPhotoViewModel : MviModelHost<FullPhotoState> {
    fun viewInGalleryApp()
    fun saveToAlbum()
}

class FullPhotoViewModel(context: Context, private val photoUri: Uri) : IFullPhotoViewModel, ViewModel() {
    override val model = model(viewModelScope, FullPhotoState(photoUri))

    @SuppressLint("StaticFieldLeak")
    private val context = context.applicationContext

    override fun viewInGalleryApp() {
        try {
            log { "photoUri $photoUri" }
            startActivity(
                context,
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(photoUri, "image/*")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                null
            )
        } catch (e: Exception) {
            toastException(context, e) { "Can not view picture in a gallery app $photoUri" }
        }
    }

    override fun saveToAlbum() = intent {
        try {
            MediaStoreUtils(context).insertFile(photoUri)
        } catch (e: Exception) {
            toastException(context, e) { "Photo insert failed: $e" }
        }
    }
}