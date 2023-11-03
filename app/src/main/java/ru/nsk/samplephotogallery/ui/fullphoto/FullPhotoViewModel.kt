package ru.nsk.samplephotogallery.ui.fullphoto

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.tools.log.log
import ru.nsk.samplephotogallery.ui.gallery.GalleryViewModel

/** TODO Use DI framework instead */
class FullPhotoViewModelFactory(private val context: Context, private val photoUri: Uri) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FullPhotoViewModel(context, photoUri) as T
    }
}
data class FullPhotoState(val photoUri: Uri)
interface IFullPhotoViewModel : MviModelHost<FullPhotoState>

class FullPhotoViewModel(context: Context, photoUri: Uri) : IFullPhotoViewModel, ViewModel() {
    override val model = model(viewModelScope, FullPhotoState(photoUri))

    init {
        log {"init"}
    }
}