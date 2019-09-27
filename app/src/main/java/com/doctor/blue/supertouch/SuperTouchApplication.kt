package com.doctor.blue.supertouch

import androidx.multidex.MultiDexApplication
import com.doctor.blue.supertouch.model.HawkHelper

class SuperTouchApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        HawkHelper.init(this)
    }
}
