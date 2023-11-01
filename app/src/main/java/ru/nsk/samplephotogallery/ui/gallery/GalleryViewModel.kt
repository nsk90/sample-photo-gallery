package ru.nsk.samplephotogallery.ui.gallery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.tools.log.MediaStoreFile
import ru.nsk.samplephotogallery.tools.log.MediaStoreUtils
import ru.nsk.samplephotogallery.tools.log.log

data class Photo(val file: MediaStoreFile)

data class GalleryState(
    val photos: List<Photo> // fixme use paging library for large data sets
)
interface IGalleryViewModel : MviModelHost<GalleryState>

class GalleryViewModel(context: Context) : IGalleryViewModel, ViewModel() {
    override val model = model(viewModelScope, GalleryState(emptyList()))

    init {
        intent {
            val images = MediaStoreUtils(context).getImages().takeLast(10)
            state {
                copy(photos = images.map { Photo(it) })
            }
        }
    }
}