package com.example.mytheater.myApp.ui

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

class FileCache @TargetApi(8) constructor(context: Context) {
    private var cacheDir: File? = null
    fun getFile(url: String): File {
        // I identify images by hashcode. Not a perfect solution, good for the
        // demo.
        val filename = url.hashCode().toString()
        // Another possible solution (thanks to grantland)
        // String filename = URLEncoder.encode(url);
        return File(cacheDir, filename)
    }

    fun clear() {
        val files = cacheDir!!.listFiles() ?: return
        for (f in files) f.delete()
    }

    init {
        // Find the dir to save cached images
        cacheDir = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File(context.getExternalFilesDir(null), "poster")
        } else {
            File(Environment.getExternalStorageDirectory(), "com.tjsahut.mytheater")
        } else context.cacheDir
        if (!cacheDir!!.exists()) cacheDir!!.mkdirs()
    }
}