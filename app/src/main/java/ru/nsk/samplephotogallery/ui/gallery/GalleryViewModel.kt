package ru.nsk.samplephotogallery.ui.gallery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost

data class Photo(val id: String)

data class GalleryState(
    val photos: List<Photo> // fixme use paging library for large data sets
)
interface IGalleryViewModel : MviModelHost<GalleryState> {
    fun onPhotoClicked(photo: Photo)
}

class GalleryViewModel(context: Context) : IGalleryViewModel, ViewModel() {
    override val model = model(viewModelScope, GalleryState(emptyList()))

    override fun onPhotoClicked(photo: Photo) {
        TODO("Not yet implemented")
    }
}