package ru.nsk.samplephotogallery.application

import android.app.Application
import android.content.Context
import ru.nsk.samplephotogallery.domain.PhotoStorage

class ThisApplication : Application() {

    lateinit var photoStorage: PhotoStorage
        private set

    override fun onCreate() {
        super.onCreate()
        photoStorage = PhotoStorage(this)
    }
}

val Context.thisApplication
    get() = applicationContext as ThisApplication