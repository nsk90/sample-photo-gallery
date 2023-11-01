package ru.nsk.samplephotogallery.ui.fullphoto

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost

data class FullPhotoState(val photoUri: Uri)
interface IFullPhotoViewModel : MviModelHost<FullPhotoState>

class FullPhotoViewModel(context: Context, photoUri: Uri) : IFullPhotoViewModel, ViewModel() {
    override val model = model(viewModelScope, FullPhotoState(photoUri))
}