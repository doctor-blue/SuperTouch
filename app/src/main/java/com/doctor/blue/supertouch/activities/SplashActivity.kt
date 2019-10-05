package com.doctor.blue.supertouch.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.base.BaseActivity

class SplashActivity : BaseActivity() {
    override fun getId(): Int {
        return R.layout.activity_splash
    }

    override fun innit() {
        val intent = Intent(this, MainActivity::class.java)
        val thread = object : Thread() {
            override fun run() {
                super.run()
                try {
                    sleep(3000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(intent)
                    finish()
                }
            }
        }
        thread.start()
    }


}
