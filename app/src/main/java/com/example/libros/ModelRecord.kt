package com.example.libros

import android.graphics.Bitmap
import java.io.Serializable

class ModelRecord(
    var id: String,
    var title: String,
    var author: String,
    var editorial: String,
    var year: String,
    var image: ByteArray?
): Serializable