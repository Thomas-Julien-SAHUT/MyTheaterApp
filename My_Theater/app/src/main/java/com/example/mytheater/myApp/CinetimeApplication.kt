package com.example.mytheater.myApp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.mytheater.R
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

@Suppress("DEPRECATION")
class CinetimeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        var channelId = "com.example.mytheater.myApp"
        var description = "Notification d'accueuil"

        var notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var intent = Intent(this, Activity::class.java)
        var pendingIntent = PendingIntent.getActivity(this,0, intent,PendingIntent.FLAG_UPDATE_CURRENT)
        var builder : Notification.Builder

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            var notificationChannel : NotificationChannel = NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.CYAN
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)

                 builder = Notification.Builder(this,channelId)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Bonjour")
                    .setContentText("Quel cinéma recherchez-vous ?")
                    .setContentIntent(pendingIntent)

        } else {
             builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Bonjour")
                .setContentText("Quel cinéma recherchez-vous ?")
                .setContentIntent(pendingIntent)
        }

        notificationManager.notify(1234, builder.build())


        val defaultOptions = DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build()
        val config = ImageLoaderConfiguration.Builder(applicationContext)
            .defaultDisplayImageOptions(defaultOptions)
            .diskCacheFileCount(300)
            .threadPoolSize(5)
            .memoryCache(WeakMemoryCache())
            .build()
        ImageLoader.getInstance().init(config)
    }
}