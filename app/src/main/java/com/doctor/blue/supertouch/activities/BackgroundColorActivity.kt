package com.doctor.blue.supertouch.activities

import android.graphics.drawable.GradientDrawable
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.base.BaseActivity
import com.doctor.blue.supertouch.model.HawkHelper
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.activity_background_color.*
import kotlinx.android.synthetic.main.layout_item_touch.*

class BackgroundColorActivity : BaseActivity() {
    private var mainSetting = HawkHelper.getMainSetting()
    override fun getId(): Int {
        return R.layout.activity_background_color
    }

    override fun innit() {
        layout_list_action.background = getBackground(mainSetting.backgroundColorTouchView)
        img_change_background_color.background = getBackground(mainSetting.backgroundColorTouchView)
        toolbar_background.setNavigationOnClickListener{finish()}
        btn_change_color.setOnClickListener {
            showDialogSelectColor()
        }
        btn_save_background_color.setOnClickListener {
            HawkHelper.saveMainSetting(mainSetting)
            finish()
        }
    }

    private fun showDialogSelectColor() {
        val pickerDialog = ColorPickerDialog.newBuilder().create()
        pickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
            override fun onColorSelected(dialogId: Int, color: Int) {
                layout_list_action.background = getBackground(color)
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
