package com.doctor.blue.supertouch.activities

import android.content.Intent
import android.view.View
import com.doctor.blue.supertouch.R

class MainActivity : BaseActivity() {
    override fun getId(): Int {
        return R.layout.activity_main
    }

    override fun innit() {

    }


    fun mainItemOnClick(view:View){
        var intent:Intent
        when(view.id){
            R.id.card_custom_menu ->{
                intent=Intent(this@MainActivity,CustomMenuActivity::class.java)
                startActivity(intent)
            }
            R.id.card_change_icon ->{}
            R.id.card_change_background ->{}
            R.id.card_privacy_policy ->{}
            R.id.card_share ->{}
            R.id.card_rate_app ->{}
        }
    }
}
