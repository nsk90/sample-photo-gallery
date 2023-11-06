package ru.nsk.samplephotogallery.ui.fullphoto

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.domain.Photo
import ru.nsk.samplephotogallery.domain.PhotoStorage
import ru.nsk.samplephotogallery.domain.MediaStoreUtils
import ru.nsk.samplephotogallery.tools.log.toastException

/** TODO Use DI framework instead */
class FullPhotoViewModelFactory(
    private val context: Context,
    private val photoStorage: PhotoStorage,
    private val initialIndex: Int,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FullPhotoViewModel(context, photoStorage,  initialIndex) as T
    }
}

data class FullPhotoState(
    val photos: List<Photo>,
    val initialIndex: Int,
)

interface IFullPhotoViewModel : MviModelHost<FullPhotoState> {
    fun viewInGalleryApp(index: Int)
    fun saveToAlbum(index: Int)
}

class FullPhotoViewModel(
    context: Context,
    photoStorage: PhotoStorage,
    initialPhotoIndex: Int
) : IFullPhotoViewModel, ViewModel() {
    override val model = model(viewModelScope, FullPhotoState(emptyList(), initialPhotoIndex))

    @SuppressLint("StaticFieldLeak")
    private val context = context.applicationContext

    init {
        intent {
            state {
                copy(
                    photos = photoStorage.getPhotos().map { Photo(it.uri) }
                )
            }
        }
    }

    override fun viewInGalleryApp(index: Int) {
        val uri = state.photos[index].uri
        try {
            startActivity(
                context,
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, "image/*")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                null
            )
        } catch (e: Exception) {
            toastException(context, e) { "Can not view picture in a gallery app $uri" }
        }
    }

    override fun saveToAlbum(index: Int) = intent {
        try {
            MediaStoreUtils(context).insertPictureToAlbum(state.photos[index].uri)
        } catch (e: Exception) {
            toastException(context, e) { "Photo insert failed: $e" }
        }
    }
}