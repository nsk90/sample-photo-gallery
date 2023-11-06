package ru.nsk.samplephotogallery.domain

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

data class Photo(val uri: Uri)

/**
 * fixme use DI for lifetime management
 */
class PhotoStorage(context: Context) {
    private val appContext = context.applicationContext
    private val images = GlobalScope.async(start = CoroutineStart.LAZY) {
        MediaStoreUtils(appContext).getImages()
    }

    suspend fun getPhotos(): List<MediaStoreFile> = images.await()
}