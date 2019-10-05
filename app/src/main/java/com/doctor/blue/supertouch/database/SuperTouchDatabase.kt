package com.doctor.blue.supertouch.database

import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.keys.Constant
import com.doctor.blue.supertouch.model.ItemTouch

object SuperTouchDatabase {
    private val normalItem = ItemTouch(Constant.idNormal, R.string.space, android.R.color.transparent)
    private val applicationItem =
        ItemTouch(Constant.idApplicationItem, R.string.application, R.drawable.ic_panel_application)
    private val ringModeItem = ItemTouch(Constant.idRingModeItem, R.string.ring_mode, R.drawable.ic_ring)
    private val backItem = ItemTouch(Constant.idBackItem, R.string.back, R.drawable.ic_backspace)
    //private val controlItem = ItemTouch(Constant.idControlItem, R.string.control, R.drawable.ic_settings)
    private val multitaskingItem =
        ItemTouch(Constant.idMultitaskingItem, R.string.multitasking, R.drawable.ic_multitasking)
    private val notificationItem =
        ItemTouch(Constant.idNotificationItem, R.string.notifications, R.drawable.ic_notifications)
    private val homeItem = ItemTouch(Constant.idHomeItem, R.string.home, R.drawable.ic_home)
    private val lockScreenItem =
        ItemTouch(Constant.idLockScreenItem, R.string.lock_screen, R.drawable.ic_lock_screen)
    private val volumeUpItem =
        ItemTouch(Constant.idVolumeUpItem, R.string.volume_up, R.drawable.ic_volume_up)
    private val volumeDownItem =
        ItemTouch(Constant.idVolumeDownItem, R.string.volume_down, R.drawable.ic_volume_down)

    val listItemMain = listOf(
        applicationItem, ringModeItem, backItem,
        multitaskingItem, notificationItem, homeItem, lockScreenItem, volumeDownItem, volumeUpItem
    )

    fun getItemTouch(idItem: String): ItemTouch {
        return when (idItem) {
            Constant.idApplicationItem -> applicationItem
            Constant.idRingModeItem -> ringModeItem
            Constant.idBackItem -> backItem
            Constant.idMultitaskingItem -> multitaskingItem
            Constant.idNotificationItem -> notificationItem
            Constant.idHomeItem -> homeItem
            Constant.idLockScreenItem -> lockScreenItem
            Constant.idVolumeDownItem -> volumeDownItem
            Constant.idVolumeUpItem -> volumeUpItem
            else -> normalItem
        }
    }
}