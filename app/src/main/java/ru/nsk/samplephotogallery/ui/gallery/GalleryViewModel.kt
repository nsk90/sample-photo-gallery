package ru.nsk.samplephotogallery.ui.gallery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.tools.MediaStoreFile
import ru.nsk.samplephotogallery.tools.MediaStoreUtils

data class Photo(val file: MediaStoreFile)

data class GalleryState(
    val photos: List<Photo> // fixme use paging library for large data sets
)

interface IGalleryViewModel : MviModelHost<GalleryState>

class GalleryViewModel(context: Context) : IGalleryViewModel, ViewModel() {
    override val model = model(viewModelScope, GalleryState(emptyList()))

    init {
        intent {
            // fixme this is naive implementation, which is not sufficient for large images collection
            val images = MediaStoreUtils(context).getImages()
            state { copy(photos = images.map { Photo(it) }) }
        }
    }
}