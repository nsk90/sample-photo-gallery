package ru.nsk.samplephotogallery.ui.gallery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.domain.Photo
import ru.nsk.samplephotogallery.domain.PhotoStorage

/** TODO Use DI framework instead */
class GalleryViewModelFactory(private val context: Context, private val photoStorage: PhotoStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GalleryViewModel(context, photoStorage) as T
    }
}

data class GalleryState(
    val photos: List<Photo> // fixme use paging library for large data sets
)

interface IGalleryViewModel : MviModelHost<GalleryState>

class GalleryViewModel(context: Context, photoStorage: PhotoStorage) : IGalleryViewModel, ViewModel() {
    override val model = model(viewModelScope, GalleryState(emptyList()))

    init {
        intent {
            state { copy(photos = photoStorage.loadPhotos().map { Photo(it.uri) }) }
        }
    }
}