package com.doctor.blue.supertouch.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class  BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getId())
        innit()
    }

    abstract fun getId(): Int
    abstract fun innit()
}