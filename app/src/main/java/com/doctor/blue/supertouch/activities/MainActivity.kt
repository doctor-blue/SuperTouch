package com.doctor.blue.supertouch.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ShareCompat
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.base.BaseActivity
import com.doctor.blue.supertouch.base.Constant
import com.doctor.blue.supertouch.event.AdminReceiver
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.layout_main_content.*

class MainActivity : BaseActivity() {
    private lateinit var devicePolicyManager: DevicePolicyManager

    private lateinit var component :ComponentName
    private val REQUEST_CODE = 123
    override fun getId(): Int {
        return R.layout.activity_main
    }

    override fun innit() {
        addEvent()
    }

    private fun addEvent() {
        //Innit event for viewZ
        sw_device_admin_permistion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDialogChangeAdminPermission()
            } else {
                devicePolicyManager =
                    getSystemService(Service.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                component = ComponentName(this, AdminReceiver::class.java)
                devicePolicyManager.removeActiveAdmin(component)
            }
        }
    }

    fun mainItemOnClick(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.card_custom_menu -> {
                intent = Intent(this@MainActivity, CustomMenuActivity::class.java)
                startActivity(intent)
            }
            R.id.card_change_icon -> {
            }
            R.id.card_change_background -> {
                intent = Intent(this@MainActivity, BackgroundColorActivity::class.java)
                startActivity(intent)
            }
            R.id.card_privacy_policy -> {
            }
            R.id.card_share -> {
                ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle("Share")
                    .setText("http://play.google.com/store/apps/details?id=" + this.packageName)
                    .startChooser()
            }
            R.id.card_rate_app -> {
                intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)
                )
                startActivity(intent)
            }
        }
    }


    @SuppressLint("InflateParams")
    private fun showDialogChangeAdminPermission() {
        val changeAdminBuilder = AlertDialog.Builder(this)

        val viewChangeAdministration =
            LayoutInflater.from(this).inflate(R.layout.dialog_change_admin_permission, null)
        val btnConfirmAdministration: MaterialButton =
            viewChangeAdministration.findViewById(R.id.btn_confirm_administration)
        val btnNoConfirmAdministration: MaterialButton =
            viewChangeAdministration.findViewById(R.id.btn_no_confirm_administration)

        changeAdminBuilder.setView(viewChangeAdministration)
        val dialogChangeAdministration = changeAdminBuilder.create()

        btnConfirmAdministration.setOnClickListener {

            devicePolicyManager = getSystemService(Service.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            component = ComponentName(this, AdminReceiver::class.java)
           val isActive= devicePolicyManager.isAdminActive(componentName)
            if (!isActive) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    resources.getString(R.string.message_permission_device_admin)
                )
                startActivityForResult(intent, REQUEST_CODE)
            }
            dialogChangeAdministration.dismiss()
        }
        btnNoConfirmAdministration.setOnClickListener {
            sw_device_admin_permistion.isChecked = false
            dialogChangeAdministration.dismiss()
        }
        dialogChangeAdministration.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            Log.e(Constant.TAG,"$resultCode \n $requestCode")
            if (resultCode == Activity.RESULT_OK) {
                Log.e(Constant.TAG," success $resultCode \n $requestCode")
                sw_device_admin_permistion.isChecked = true
            }else{
                Log.e(Constant.TAG," fail $resultCode \n $requestCode")
                sw_device_admin_permistion.isChecked=false
            }
        } else
        super.onActivityResult(requestCode, resultCode, data)

    }
}
