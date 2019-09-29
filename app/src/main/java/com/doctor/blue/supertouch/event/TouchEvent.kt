package com.doctor.blue.supertouch.event

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.activities.MainActivity
import com.doctor.blue.supertouch.keys.Constant
import com.doctor.blue.supertouch.model.HawkHelper

@SuppressLint("StaticFieldLeak")
object TouchEvent {
    lateinit var imgTouch: ImageView
    lateinit var txtNameTouch: TextView
    lateinit var context: Context
    private val mainSetting = HawkHelper.getMainSetting()
    lateinit var activity: Activity
    fun mainMenuEvent(id: String) {
        when (id) {
            Constant.idNormal -> {
            }
            Constant.idVolumeUpItem -> {
                volumeUpEvent()
            }
            Constant.idVolumeDownItem -> {
                volumeDownEvent()
            }
            Constant.idLockScreenItem -> {
                lockScreenEvent()
            }
            Constant.idHomeItem -> {
                backHomeEvent()
            }
            Constant.idNotificationItem -> {
            }
            Constant.idMultitaskingItem -> {
            }
            Constant.idControlItem -> {
            }
            Constant.idBackItem -> {
            }
            Constant.idRingModeItem -> {
                ringModeEvent()
            }
            Constant.idApplicationItem -> {
            }
        }
    }

    private fun backHomeEvent() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(homeIntent)
    }

    var isRingMode = false
    private fun ringModeEvent() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mainSetting.isRingMode) {
                isRingMode = !isRingMode
                val audioManager: AudioManager =
                    context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                if (isRingMode) {
                    txtNameTouch.text = context.resources.getString(R.string.ring)
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                } else {
                    txtNameTouch.text = context.resources.getString(R.string.silent)
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            } else {
//                val intent = Intent(context, OnresultAcitvity2::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                context.startActivity(intent)
            }
        } else {
            isRingMode = !isRingMode
            val audioManager: AudioManager =
                context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (isRingMode) {
                txtNameTouch.text = context.resources.getString(R.string.ring)
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
            } else {
                txtNameTouch.text = context.getString(R.string.silent)
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun lockScreenEvent() {
        val devicePolicyManager =
            context.getSystemService(Service.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(context, AdminReceiver::class.java)
        val isActive = devicePolicyManager.isAdminActive(componentName)
        if (mainSetting.isAdministrator && isActive) {
            devicePolicyManager.lockNow()
        } else {
            Toast.makeText(
                context,
                activity.resources.getString(R.string.notice_grant_device_admin),
                Toast.LENGTH_SHORT
            )
            val intent= Intent(activity, MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    private fun volumeDownEvent() {
        val audio: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun volumeUpEvent() {
        val audio: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }
}