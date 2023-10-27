package ru.nsk.samplephotogallery.tools.log

import android.content.Context
import android.util.Log
import android.widget.Toast

private const val DEFAULT_TAG = "photoGallery"

fun Any.toast(
    context: Context,
    tag: String = this::class.simpleName ?: DEFAULT_TAG,
    lazyText: () -> String,
) {
    val text = lazyText()
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    log(tag, text)
}

fun Any.toastException(
    context: Context,
    exception: Throwable,
    tag: String = this::class.simpleName ?: DEFAULT_TAG,
    lazyText: Throwable.() -> String,
) {
    val text = exception.lazyText()
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    logException(exception, tag = tag, text = text)
}

fun Any.log(
    tag: String = this::class.simpleName ?: DEFAULT_TAG,
    lazyText: () -> String,
) {
    log(tag = tag, text = lazyText())
}

fun Any.logException(
    exception: Throwable,
    tag: String = this::class.simpleName ?: DEFAULT_TAG,
    lazyText: Throwable.() -> String,
) {
    logException(exception, tag = tag, text = exception.lazyText())
}

private fun log(tag: String, text: String) {
    Log.i(tag, text)
}

private fun logException(exception: Throwable, tag: String, text: String) {
    Log.w(tag, text, exception)
}