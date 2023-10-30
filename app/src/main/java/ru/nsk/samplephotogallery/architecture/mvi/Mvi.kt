package ru.nsk.samplephotogallery.architecture.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class MviModel<State : Any>(val scope: CoroutineScope, initialState: State) {
    private val _stateFlow = MutableStateFlow(initialState)
    val stateFlow = _stateFlow.asStateFlow()

    fun state(block: State.() -> State) {
        _stateFlow.value = _stateFlow.value.block()
    }
}

/**
 * Typically a ViewModel implements this interface
 */
interface MviModelHost<State : Any> {
    val model: MviModel<State>

    fun <State : Any> MviModelHost<State>.model(scope: CoroutineScope, initialState: State) =
        MviModel(scope, initialState)

    /**
     * This block is used to change model state and emit effects
     */
    fun intent(context: CoroutineContext = EmptyCoroutineContext, block: suspend MviModel<State>.() -> Unit) {
        model.scope.launch(context) { model.block() }
    }

    val state: State get() = model.stateFlow.value
}