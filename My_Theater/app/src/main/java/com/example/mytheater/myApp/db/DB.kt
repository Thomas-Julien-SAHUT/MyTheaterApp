package com.example.mytheater.myApp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DB internal constructor(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL("CREATE TABLE favorites ( _id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT NOT NULL, title TEXT NOT NULL, location TEXT NOT NULL, city TEXT NOT NULL DEFAULT '')")
    }

    override fun onUpgrade(database: SQLiteDatabase, oldV: Int, newV: Int) {
        // See
        // http://www.drdobbs.com/database/using-sqlite-on-android/232900584?pgno=2
        if (oldV < 2 && newV >= 2) {
            upgradeV2(database)
        }
    }

    private fun upgradeV2(database: SQLiteDatabase) {
        Log.i("DB", "Upgrading database")
        database.execSQL("ALTER TABLE favorites ADD COLUMN city TEXT NOT NULL DEFAULT ''")
    }

    companion object {
        private const val DB_VERSION = 2
        private const val DB_NAME = "mytheater.s3db"
    }
}