package com.example.mytheater.myApp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Poster(var level: Int, var levelRequested: Int) {
    var bmpLow: Bitmap? = null
    var bmpMed: Bitmap? = null
    var bmpHigh: Bitmap? = null
    fun continueLoading(): Boolean {
        return level < levelRequested
    }

    fun getBmp(level: Int): Bitmap? {
        return when (level) {
            1 -> getBmpLow()?.let { Bitmap.createScaledBitmap(it, 150, 200, false) }
            2 -> getBmpMed()?.let { Bitmap.createScaledBitmap(it, 200, 267, false) }
            3 -> getBmpHigh()
            else -> null
        }
    }

    @JvmName("getBmpLow1")
    private fun getBmpLow(): Bitmap? {
        return if (bmpLow != null) bmpLow else stub
    }

    @JvmName("getBmpMed1")
    private fun getBmpMed(): Bitmap? {
        return if (bmpMed != null) bmpMed else getBmpLow()
    }

    @JvmName("getBmpHigh1")
    private fun getBmpHigh(): Bitmap? {
        return if (bmpHigh != null) bmpHigh else getBmpMed()
    }

    fun setBmp(bmp: Bitmap?, level: Int) {
        when (level) {
            1 -> bmpLow = bmp
            2 -> bmpMed = bmp
            3 -> bmpHigh = bmp
            else -> {
            }
        }
    }

    fun setCurrentBmp(bmp: Bitmap?) {
        when (level) {
            1 -> bmpLow = bmp
            2 -> bmpMed = bmp
            3 -> bmpHigh = bmp
            else -> {
            }
        }
    }

    val bytes: Int
        get() {
            var size = 0
            if (bmpLow != null) {
                size += bmpLow!!.rowBytes * bmpLow!!.height
            }
            if (bmpMed != null) {
                size += bmpMed!!.rowBytes * bmpMed!!.height
            }
            if (bmpHigh != null) {
                size += bmpHigh!!.rowBytes * bmpHigh!!.height
            }
            size += Integer.SIZE * 2
            return size
        }

    companion object {
        var stub: Bitmap? = null
        fun getStub(level: Int): Bitmap? {
            return when (level) {
                1 -> stub?.let { Bitmap.createScaledBitmap(it, 150, 200, false) }
                2 -> stub?.let { Bitmap.createScaledBitmap(it, 200, 267, false) }
                3 -> stub
                else -> null
            }
        }

        fun generateStub(ctx: Context, stub_id: Int) {
            val bitmap = BitmapFactory.decodeResource(ctx.resources, stub_id)
            stub = bitmap
        }
    }
}