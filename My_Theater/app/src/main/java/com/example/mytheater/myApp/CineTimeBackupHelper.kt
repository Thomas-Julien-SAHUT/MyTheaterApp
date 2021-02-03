package com.example.mytheater.myApp

import android.annotation.TargetApi
import android.app.backup.*
import android.os.Build
import android.os.ParcelFileDescriptor
import com.example.mytheater.myApp.db.DBHelper
import java.io.IOException

@TargetApi(Build.VERSION_CODES.FROYO)
class CineTimeBackupHelper : BackupAgentHelper() {
    // Allocate a helper and add it to the backup agent
    override fun onCreate() {
        val helper = SharedPreferencesBackupHelper(this, PREFS)
        addHelper(PREFS_BACKUP_KEY, helper)
        val helperF = FileBackupHelper(this, DB)
        addHelper(FILES_BACKUP_KEY, helperF)
    }

    @Throws(IOException::class)
    override fun onBackup(
        oldState: ParcelFileDescriptor,
        data: BackupDataOutput,
        newState: ParcelFileDescriptor
    ) {
        // Hold the lock while the FileBackupHelper performs backup
        synchronized(DBHelper.sDataLock) { super.onBackup(oldState, data, newState) }
    }

    @Throws(IOException::class)
    override fun onRestore(
        data: BackupDataInput,
        appVersionCode: Int,
        newState: ParcelFileDescriptor
    ) {
        // Hold the lock while the FileBackupHelper restores the file
        synchronized(DBHelper.sDataLock) { super.onRestore(data, appVersionCode, newState) }
    }

    companion object {
        // The name of the SharedPreferences file
        const val PREFS = "synopsis"

        // A key to uniquely identify the set of backup data
        const val PREFS_BACKUP_KEY = "prefs"

        // The name of the SharedPreferences file
        const val DB = "../databases/cintetime.s3db"

        // A key to uniquely identify the set of backup data
        const val FILES_BACKUP_KEY = "files"
    }
}