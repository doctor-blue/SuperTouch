package com.doctor.blue.supertouch.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.adapter.ApplicationAdapter
import com.doctor.blue.supertouch.base.BaseActivity
import com.doctor.blue.supertouch.keys.Constant
import com.doctor.blue.supertouch.model.HawkHelper
import kotlinx.android.synthetic.main.activity_list_application.*

class ListApplicationActivity : BaseActivity() {
    lateinit var applicationAdapter: ApplicationAdapter
    private val listApplication: MutableList<String> = mutableListOf()
    private val itemApplication = HawkHelper.getItemApplication()
    private var packageAppName: String = ""
    override fun getId(): Int {
        return R.layout.activity_list_application
    }

    override fun innit() {
        toolbar_list_app.setNavigationOnClickListener { finish() }
        val itemApp: String? = intent.getStringExtra(Constant.PICK_APP)
        getListApplication()
        applicationAdapter = ApplicationAdapter(this, listApplication) {
            packageAppName = it
        }
        rv_application.setHasFixedSize(true)
        rv_application.layoutManager = GridLayoutManager(this, 4)
        rv_application.adapter = applicationAdapter
        btn_save_application.setOnClickListener {
            itemApp?.let {
                savePackageName(it)
                finish()
            }
        }
    }

    private fun getListApplication() {
        val packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packages = packageManager.queryIntentActivities(intent, 0)
        for (resolve_info in packages) {
            try {
                val packageName = resolve_info.activityInfo.packageName

                var same = false
                for (i in 0 until listApplication.size) {
                    if (packageName == listApplication[i])
                        same = true
                }
                if (!same) {
                    listApplication.add(packageName)
                }
            } catch (e: Exception) {
            }

        }
    }

    private fun savePackageName(itemApp: String) {
        when (itemApp) {
            Constant.itemApp11 -> itemApplication.packageName11 = packageAppName
            Constant.itemApp12 -> itemApplication.packageName12 = packageAppName
            Constant.itemApp13 -> itemApplication.packageName13 = packageAppName
            Constant.itemApp21 -> itemApplication.packageName21 = packageAppName
            Constant.itemApp23 -> itemApplication.packageName23 = packageAppName
            Constant.itemApp31 -> itemApplication.packageName31 = packageAppName
            Constant.itemApp32 -> itemApplication.packageName32 = packageAppName
            else -> itemApplication.packageName33 = packageAppName
        }
        HawkHelper.saveItemApplication(itemApplication)
    }
}
