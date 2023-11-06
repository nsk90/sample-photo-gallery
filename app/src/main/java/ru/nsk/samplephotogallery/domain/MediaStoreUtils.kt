/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.nsk.samplephotogallery.domain

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build.*
import android.os.Environment
import android.provider.MediaStore.Images
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val ALBUM_NAME = "samplePhotoGallery"

/**
 * A utility class for accessing this app's photo storage.
 *
 * Since this app doesn't request any external storage permissions, it will only be able to access
 * photos taken with this app. If the app is uninstalled, the photos taken with this app will stay
 * on the device, but reinstalling the app will not give it access to photos taken with the app's
 * previous instance. You can request further permissions to change this app's access. See this
 * guide for more: https://developer.android.com/training/data-storage.
 */
// fixme Copy pasted code, make refactoring
class MediaStoreUtils(private val context: Context) {

    val mediaStoreCollection: Uri? = if (VERSION.SDK_INT >= VERSION_CODES.Q) {
        Images.Media.EXTERNAL_CONTENT_URI
    } else {
        context.getExternalFilesDir(null)?.toUri()
    }

    private suspend fun getMediaStoreImageCursor(mediaStoreCollection: Uri): Cursor? {
        return withContext(Dispatchers.IO) {
            val projection = arrayOf(imageDataColumnIndex, imageIdColumnIndex)
            val sortOrder = "DATE_ADDED DESC"
            context.contentResolver.query(
                mediaStoreCollection, projection, null, null, sortOrder
            )
        }
    }

    /**
     * Fixme add old android versions support
     */
    suspend fun insertPictureToAlbum(uri: Uri) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(Images.Media.MIME_TYPE, "image/jpeg")
            // Add the date meta data to ensure the image is added at the front of the gallery
            val millis = System.currentTimeMillis()
            put(Images.Media.DATE_ADDED, millis / 1000L)
            put(Images.Media.DATE_MODIFIED, millis / 1000L)
            put(Images.Media.DATE_TAKEN, millis)
            put(Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$ALBUM_NAME")
        }

        val resolver = context.contentResolver
        resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)?.let { insertUri ->
            try {
                resolver.openInputStream(uri)!!.buffered().use { inputStream ->
                    resolver.openOutputStream(insertUri)!!.buffered().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                resolver.delete(insertUri, null, null)
                throw IllegalStateException("Insert failed", e)
            }
        }
    }

    suspend fun getLatestImageFilename(): String? {
        var filename: String?
        if (mediaStoreCollection == null) return null

        getMediaStoreImageCursor(mediaStoreCollection).use { cursor ->
            if (cursor?.moveToFirst() != true) return null
            filename = cursor.getString(cursor.getColumnIndexOrThrow(imageDataColumnIndex))
        }

        return filename
    }

    suspend fun getImages(): MutableList<MediaStoreFile> {
        val files = mutableListOf<MediaStoreFile>()
        if (mediaStoreCollection == null) return files

        getMediaStoreImageCursor(mediaStoreCollection).use { cursor ->
            val imageDataColumn = cursor?.getColumnIndexOrThrow(imageDataColumnIndex)
            val imageIdColumn = cursor?.getColumnIndexOrThrow(imageIdColumnIndex)

            if (cursor != null && imageDataColumn != null && imageIdColumn != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(imageIdColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val contentFile = File(cursor.getString(imageDataColumn))
                    files.add(MediaStoreFile(contentUri, contentFile, id))
                }
            }
        }

        return files
    }

    companion object {
        // Suppress DATA index deprecation warning since we need the file location for the Glide library
        @Suppress("DEPRECATION")
        private const val imageDataColumnIndex = Images.Media.DATA
        private const val imageIdColumnIndex = Images.Media._ID
    }
}

data class MediaStoreFile(val uri: Uri, val file: File, val id: Long)