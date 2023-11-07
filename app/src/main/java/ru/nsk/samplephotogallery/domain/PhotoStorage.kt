package ru.nsk.samplephotogallery.domain

import android.content.Context
import android.net.Uri

data class Photo(val uri: Uri)

/**
 * fixme use DI for lifetime management
 */
class PhotoStorage(context: Context) {
    private val appContext = context.applicationContext
    private var images: List<MediaStoreFile> = emptyList()

    suspend fun loadPhotos(): List<MediaStoreFile> {
        return MediaStoreUtils(appContext).getImages().also { images = it }
    }

    fun getPhotos(): List<MediaStoreFile> = images
}