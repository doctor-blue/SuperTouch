package com.doctor.blue.supertouch.event

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.doctor.blue.supertouch.keys.Constant

class AdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context?, intent: Intent?) {
        Log.e(Constant.TAG,"on Administration")
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        Log.e(Constant.TAG,"off Administration")
    }
}