package com.example.libros

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.media.Image
import java.io.ByteArrayOutputStream
import java.lang.Exception


class DatabaseHelper(context: Context?): SQLiteOpenHelper(
    context,
    Contants.DB_NAME,
    null,
    Contants.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(Contants.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + Contants.TABLE_NAME)
        onCreate(db)
    }

    fun insertBook(
        title: String?,
        author: String?,
        editorial: String?,
        year: String?,
        image: Bitmap?
        ) : Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(Contants.C_TITLE, title)
        values.put(Contants.C_AUTHOR, author)
        values.put(Contants.C_EDITORIAL, editorial)
        values.put(Contants.C_YEAR, year)
        values.put(Contants.C_IMAGE, image?.toBlob())

        val id = db.insert(Contants.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun updateBook(book: ModelRecord) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(Contants.C_TITLE, book.title)
        values.put(Contants.C_AUTHOR, book.author)
        values.put(Contants.C_EDITORIAL, book.editorial)
        values.put(Contants.C_YEAR, book.year)
        values.put(Contants.C_IMAGE, book.image)

        db.update(Contants.TABLE_NAME, values, "${Contants.C_ID} = ${book.id}", null)
        db.close()
    }

    fun searchRecords(query: String): ArrayList<ModelRecord>{
        val recordList = ArrayList<ModelRecord>()
        val selectQuery = "SELECT * FROM ${Contants.TABLE_NAME} WHERE ${Contants.C_TITLE} like '% $query%'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if(cursor.moveToNext()) {
            do {
                val modelRecord = ModelRecord(
                    ""+ cursor.getInt(cursor.getColumnIndexOrThrow(Contants.C_ID)),
                    ""+ cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_TITLE)),
                ""+ cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_AUTHOR)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_EDITORIAL)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_YEAR)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(Contants.C_IMAGE))
                )
                recordList.add(modelRecord)
            } while (cursor.moveToNext())
        }

        db.close()
        return recordList
    }

    fun getAllRecords(): ArrayList<ModelRecord>{
        val recordList = ArrayList<ModelRecord>()
        val selectQuery = "SELECT * FROM ${Contants.TABLE_NAME}"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if(cursor.moveToNext()) {
            do {
                val modelRecord = ModelRecord(
                    ""+ cursor.getInt(cursor.getColumnIndexOrThrow(Contants.C_ID)),
                    ""+ cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_TITLE)),
                    ""+ cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_AUTHOR)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_EDITORIAL)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(Contants.C_YEAR)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(Contants.C_IMAGE))
                )
                recordList.add(modelRecord)
            } while (cursor.moveToNext())
        }

        db.close()
        return recordList
    }
}

fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Bitmap.toBlob(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    compress(CompressFormat.PNG, 0, outputStream)
    return outputStream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeByteArray(this, 0, size)
    } catch(e: Exception) {
        null
    }
}