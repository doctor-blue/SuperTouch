package com.doctor.blue.supertouch.service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.doctor.blue.supertouch.keys.Constant
import com.doctor.blue.supertouch.model.HawkHelper

class SuperTouchAccessibilityService : AccessibilityService() {

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action.equals(Constant.actionMultitasking))
            multitaskingEvent()
        if (action.equals(Constant.actionBackSpace))
            backEvent()
        if (action.equals(Constant.actionNotification))
            notificationEvent()
        return Service.START_NOT_STICKY
    }

    private fun multitaskingEvent() {
        try {
            performGlobalAction(GLOBAL_ACTION_RECENTS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun backEvent() {
        try {
            performGlobalAction(GLOBAL_ACTION_BACK)
        } catch (e: Exception) {
            Log.e("hihi", e.toString())
            e.printStackTrace()
        }
    }

    private fun notificationEvent() {
        try {
            performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("hehe", "connected")
        val mainSetting = HawkHelper.getMainSetting()
        mainSetting.isAccessibilityConnected = true
        HawkHelper.saveMainSetting(mainSetting)
    }
}