package com.doctor.blue.supertouch.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.adapter.ActionAdapter
import com.doctor.blue.supertouch.base.BaseActivity
import com.doctor.blue.supertouch.database.SuperTouchDatabase
import com.doctor.blue.supertouch.model.HawkHelper
import kotlinx.android.synthetic.main.activity_custom_menu.*

class CustomMenuActivity : BaseActivity() {
    private val mainMenuSetting = HawkHelper.getMainMenuSetting()
    private lateinit var actionAdapter: ActionAdapter
    private var idAction = ""
    private val mainSetting=HawkHelper.getMainSetting()
    override fun getId(): Int {
        return R.layout.activity_custom_menu
    }

    override fun innit() {
        innitControl()
        btn_save_custom_menu.setOnClickListener {
            HawkHelper.saveMainMenuSetting(mainMenuSetting)
            finish()
        }
        toolbar_custom_menu.setNavigationOnClickListener { finish() }
    }

    private fun innitControl() {
        val getIconId: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).iconItem }
        val getNameItemId: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).nameItem }
        touch_view_custom_menu.setIconItem(
            getIconId(mainMenuSetting.idItem11),
            getIconId(mainMenuSetting.idItem12),
            getIconId(mainMenuSetting.idItem13),
            getIconId(mainMenuSetting.idItem21),
            getIconId(mainMenuSetting.idItem23),
            getIconId(mainMenuSetting.idItem31),
            getIconId(mainMenuSetting.idItem32),
            getIconId(mainMenuSetting.idItem33)
        )
        touch_view_custom_menu.setTextItem(
            getNameItemId(mainMenuSetting.idItem11),
            getNameItemId(mainMenuSetting.idItem12),
            getNameItemId(mainMenuSetting.idItem13),
            getNameItemId(mainMenuSetting.idItem21),
            getNameItemId(mainMenuSetting.idItem23),
            getNameItemId(mainMenuSetting.idItem31),
            getNameItemId(mainMenuSetting.idItem32),
            getNameItemId(mainMenuSetting.idItem33)
            )
        touch_view_custom_menu.setBackgroundColorTouchView(mainSetting.backgroundColorTouchView)
    }

/*
    fun customMenuItemOnClick(view: View) {
        when (view) {
            img_item_custom_menu_1_1 -> {
                showDialogSelectAction(img_item_custom_menu_1_1, txt_item_custom_menu_1_1)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem11 = idAction
            }
            img_item_custom_menu_1_2 -> {
                showDialogSelectAction(img_item_custom_menu_1_2, txt_item_custom_menu_1_2)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem12 = idAction
            }
            img_item_custom_menu_1_3 -> {
                showDialogSelectAction(img_item_custom_menu_1_3, txt_item_custom_menu_1_3)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem13 = idAction
            }
            img_item_custom_menu_2_1 -> {
                showDialogSelectAction(img_item_custom_menu_2_1, txt_item_custom_menu_2_1)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem21 = idAction
            }
            img_item_custom_menu_2_3 -> {
                showDialogSelectAction(img_item_custom_menu_2_3, txt_item_custom_menu_2_3)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem23 = idAction
            }
            img_item_custom_menu_3_1 -> {
                showDialogSelectAction(img_item_custom_menu_3_1, txt_item_custom_menu_3_1)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem31 = idAction
            }
            img_item_custom_menu_3_2 -> {
                showDialogSelectAction(img_item_custom_menu_3_2, txt_item_custom_menu_3_2)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem32 = idAction
            }
            img_item_custom_menu_3_3 -> {
                showDialogSelectAction(img_item_custom_menu_3_3, txt_item_custom_menu_3_3)
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem33 = idAction
            }
        }
    }
*/

/*
    private fun checkChangeAction(img:ImageView){
        when (img) {
            img_item_custom_menu_1_1 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem11 = idAction
            }
            img_item_custom_menu_1_2 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem12 = idAction
            }
            img_item_custom_menu_1_3 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem13 = idAction
            }
            img_item_custom_menu_2_1 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem21 = idAction
            }
            img_item_custom_menu_2_3 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem23 = idAction
            }
            img_item_custom_menu_3_1 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem31 = idAction
            }
            img_item_custom_menu_3_2 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem32 = idAction
            }
            img_item_custom_menu_3_3 -> {
                if (idAction.isNotEmpty())
                    mainMenuSetting.idItem33 = idAction
            }
        }
    }
*/

    @SuppressLint("InflateParams")
    private fun showDialogSelectAction(imgCustomMenu: ImageView, txtCustomMenu: TextView) {
        val builder = AlertDialog.Builder(this@CustomMenuActivity)
        val view = LayoutInflater.from(this@CustomMenuActivity)
            .inflate(R.layout.dialog_select_action, null)
        val rvAction = view.findViewById<RecyclerView>(R.id.rv_action)
        builder.setView(view)
        val dialog = builder.create()
        actionAdapter = ActionAdapter(this@CustomMenuActivity) {
            imgCustomMenu.setImageResource(it.iconItem)
            txtCustomMenu.text = resources.getString(it.nameItem)
            idAction = it.idItem
            //checkChangeAction(imgCustomMenu)
            dialog.dismiss()
        }
        rvAction.setHasFixedSize(true)
        rvAction.layoutManager = GridLayoutManager(this@CustomMenuActivity, 2)
        rvAction.adapter = actionAdapter
        dialog.show()
    }


}
