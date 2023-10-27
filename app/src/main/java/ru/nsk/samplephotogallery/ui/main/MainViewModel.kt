package ru.nsk.samplephotogallery.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.nsk.samplephotogallery.architecture.mvi.MviModelHost
import ru.nsk.samplephotogallery.tools.log.log

class MainState

class MainViewModel : ViewModel(), MviModelHost<MainState> {
    override val model = model(viewModelScope, MainState())

    init {
        log { "init" }
    }
    fun takePicture() = intent {
        
    }
}