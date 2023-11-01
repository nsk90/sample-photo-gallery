package ru.nsk.samplephotogallery.tools

import android.net.Uri
import java.net.URLEncoder

fun Uri.encoded() = URLEncoder.encode(toString(), "utf-8")