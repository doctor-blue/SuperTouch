package com.doctor.blue.supertouch

import androidx.multidex.MultiDexApplication

class SuperTouchApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        HawkHelper.init(this)
        if (BuildConfig.DEBUG) {
            LogUtils.enable(true)
        }
    }
}
