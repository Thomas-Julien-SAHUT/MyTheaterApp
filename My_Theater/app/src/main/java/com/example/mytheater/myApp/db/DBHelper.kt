package com.example.mytheater.myApp.db

import android.annotation.SuppressLint
import android.app.backup.BackupManager
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.mytheater.myApp.objects.Theater
import java.util.*
import kotlin.collections.ArrayList

object DBHelper {
    @JvmField
    val sDataLock = Any()
    internal var favCodes = ArrayList<String>()
    private fun getDatabase(context: Context): SQLiteDatabase {
        val db = DB(context)
        return db.readableDatabase
    }

    /**
     * Insert new item into favorites
     *
     * @param context
     */
    @SuppressLint("NewApi")
    fun insertFavorite(context: Context, code: String, title: String?, location: String?, city: String?) {
        synchronized(sDataLock) {
            val db = getDatabase(context)
            val values = ContentValues()
            values.put("code", code)
            values.put("title", title)
            values.put("location", location)
            values.put("city", city)
            db.insert("favorites", null, values)
            db.close()
        }
        favCodes.add(code)
        BackupManager(context).dataChanged()
    }

    /**
     * Retrieve favorites
     */
    @JvmStatic
    fun getFavorites(context: Context): ArrayList<Theater>? {
        val favorites : ArrayList<Theater>? = ArrayList<Theater>()
        synchronized(sDataLock) {
            val db = getDatabase(context)
            favCodes = ArrayList()

            // Cursor query (boolean distinct, String table, String[] columns,
            // String selection, String[] selectionArgs, String groupBy, String
            // having, String orderBy, String limit)
            val cursor = db.query(true, "favorites", arrayOf("code", "title", "location", "city"), null, null, null, null, "_id DESC", "100")
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val entry = Theater()
                entry.code = cursor.getString(0)
                entry.title = cursor.getString(1)
                entry.location = cursor.getString(2)
                entry.city = cursor.getString(3)
                favorites!!.add(entry)
                favCodes.add(entry.code.toString())
                cursor.moveToNext()
            }
            cursor.close()
            db.close()
        }
        return favorites
    }

    @SuppressLint("NewApi")
    fun removeFavorite(context: Context, code: String) {
        synchronized(sDataLock) {
            val db = getDatabase(context)
            db.delete("favorites", "code = ?", arrayOf(code))
            db.close()
        }
        favCodes.remove(code)
        BackupManager(context).dataChanged()
    }

    fun isFavorite(code: String): Boolean {
        return favCodes.contains(code)
    }
}