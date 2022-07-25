package com.example.libros

object Contants {


    const val DB_NAME = "BOOKS_INFO"
    const val DB_VERSION = 1

    const val TABLE_NAME = "BOOK_INFO_TABLE"

    const val C_ID = "ID"
    const val C_TITLE = "TITLE"
    const val C_AUTHOR = "AUTHOR"
    const val C_EDITORIAL = "EDITORIAL"
    const val C_YEAR = "YEAR"
    const val C_GENDER = "GENDER"
    const val C_PRICE = "PRICE"
    const val C_IMAGE = "IMAGE"


    const val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ("
            + "$C_ID INTEGER PRIMARY KEY  AUTOINCREMENT,"
            + "$C_TITLE TEXT,"
            + "$C_AUTHOR TEXT,"
            + "$C_EDITORIAL TEXT,"
            + "$C_YEAR TEXT,"
            + "$C_GENDER TEXT,"
            + "$C_PRICE TEXT,"
            + "$C_IMAGE BLOB )")
}