package com.doctor.blue.supertouch.activities

import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import android.widget.Toast
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.base.BaseActivity
import com.doctor.blue.supertouch.database.SuperTouchDatabase
import com.doctor.blue.supertouch.model.HawkHelper
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.activity_background_color.*
import kotlinx.android.synthetic.main.activity_custom_menu.*
import kotlinx.android.synthetic.main.layout_touch.*
import kotlinx.android.synthetic.main.touch_view.view.*


class BackgroundColorActivity : BaseActivity() {
    private var mainSetting = HawkHelper.getMainSetting()
    private var mainMenuSetting=HawkHelper.getMainMenuSetting()
    override fun getId(): Int {
        return R.layout.activity_background_color
    }

    override fun innit() {
        touch_view_background_color.setBackgroundColorTouchView(mainSetting.backgroundColorTouchView)

        img_change_background_color.background = getBackground(mainSetting.backgroundColorTouchView)

        toolbar_background.setNavigationOnClickListener { finish() }
        btn_change_color.setOnClickListener {
            showDialogSelectColor()
        }
        btn_save_background_color.setOnClickListener {
            HawkHelper.saveMainSetting(mainSetting)
            finish()
        }

        val getIconId: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).iconItem }
        val getTitleItem: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).nameItem }

        touch_view_background_color.setIconItem(
            getIconId(mainMenuSetting.idItem11),
            getIconId(mainMenuSetting.idItem12),
            getIconId(mainMenuSetting.idItem13),
            getIconId(mainMenuSetting.idItem21),
            getIconId(mainMenuSetting.idItem23),
            getIconId(mainMenuSetting.idItem31),
            getIconId(mainMenuSetting.idItem32),
            getIconId(mainMenuSetting.idItem33)
        )
        touch_view_background_color.setTextItem(
            getTitleItem(mainMenuSetting.idItem11),
            getTitleItem(mainMenuSetting.idItem12),
            getTitleItem(mainMenuSetting.idItem13),
            getTitleItem(mainMenuSetting.idItem21),
            getTitleItem(mainMenuSetting.idItem23),
            getTitleItem(mainMenuSetting.idItem31),
            getTitleItem(mainMenuSetting.idItem32),
            getTitleItem(mainMenuSetting.idItem33)
        )
    }

    private fun showDialogSelectColor() {
        val pickerDialog = ColorPickerDialog.newBuilder().create()
        pickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
            override fun onColorSelected(dialogId: Int, color: Int) {
                touch_view_background_color.setBackgroundColorTouchView(color)
                img_change_background_color.background = getBackground(color)
                mainSetting.backgroundColorTouchView = color
            }

            override fun onDialogDismissed(dialogId: Int) {

            }
        })
        pickerDialog.show(supportFragmentManager, "Pick color")
    }

    private fun getBackground(color: Int): GradientDrawable {
        val shape = GradientDrawable()
        shape.cornerRadius = 15.0f
        shape.setColor(color)
        return shape
    }


}
