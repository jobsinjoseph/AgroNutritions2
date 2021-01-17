package com.agronutritions.shop.app

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.room.Room
import com.agronutritions.shop.constants.AppConstants.Companion.DB_NAME
import com.agronutritions.shop.constants.SharedPrefereceConstants.Companion.PREF_NAME
import com.agronutritions.shop.db.AppDb
import com.agronutritions.shop.log.AppLogger
import com.agronutritions.shop.preference.Preference
import com.razorpay.Checkout
import org.jetbrains.anko.getStackTraceString


class App : Application() {
    private var mDatabase: AppDb? = null

    companion object {
        lateinit var mApp: App
    }

    override fun onCreate() {
        mApp = this
        super.onCreate()

        mDatabase = Room.databaseBuilder(
            applicationContext,
            AppDb::class.java,
            DB_NAME
        ).allowMainThreadQueries()
            .build()

        Preference.init(this, PREF_NAME)

        Checkout.preload(this)

        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            AppLogger.error(e.getStackTraceString())
        }
    }

    override fun onTerminate() {
        mDatabase = null
        super.onTerminate()
    }
    fun <T> getDBLocked(block: (db: AppDb?) -> T) {
        synchronized(mApp) {
            block(mDatabase)
        }
    }

    fun getDbInstance():AppDb{
        return mDatabase!!
    }

    fun getAppVersion(): String{
        val manager: PackageManager = mApp.packageManager
        val info: PackageInfo = manager.getPackageInfo(
            mApp.packageName, 0
        )
        return info.versionName
    }
}