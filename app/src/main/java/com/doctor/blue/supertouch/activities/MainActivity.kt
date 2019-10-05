package com.doctor.blue.supertouch.activities

import android.annotation.SuppressLint
import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.adapter.IconAdapter
import com.doctor.blue.supertouch.base.BaseActivity
import com.doctor.blue.supertouch.event.AdminReceiver
import com.doctor.blue.supertouch.event.TouchEvent
import com.doctor.blue.supertouch.keys.Constant
import com.doctor.blue.supertouch.model.HawkHelper
import com.doctor.blue.supertouch.model.MainSetting
import com.doctor.blue.supertouch.service.SuperTouchService
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_content.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var component: ComponentName
    private lateinit var mainSetting: MainSetting
    lateinit var pathBGs: ArrayList<String>
    lateinit var assetManager: AssetManager
    lateinit var iconAdapter: IconAdapter

    companion object {
        private const val REQUEST_CODE = 123
        const val ON_DO_NOT_DISTURB_CALLBACK_CODE: Int=11
    }

    override fun getId(): Int {
        return R.layout.activity_main
    }

    override fun innit() {
        assetManager = assets
        pathBGs = ArrayList()

        if (Build.VERSION.SDK_INT>=23){
            requestPermistion()
        }

        ImagePath().execute()
        addEvent()

        mainSetting = HawkHelper.getMainSetting()

        Glide.with(this)
            .load(Uri.fromFile(File("//android_asset/image/" + mainSetting.nameIcon)))
            .apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(64, 64)
            )
            .into(img_preview_icon)

        sw_device_admin_permistion.isChecked = mainSetting.isAdministrator
        sw_active_touch.isChecked = checkServiceRunning(SuperTouchService::class.java)

    }

    private fun addEvent() {
        //Innit event for view
        sw_device_admin_permistion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!mainSetting.isAdministrator) {
                    showDialogChangeAdminPermission()
                }
            } else {
                devicePolicyManager =
                    getSystemService(Service.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                component = ComponentName(this, AdminReceiver::class.java)
                devicePolicyManager.removeActiveAdmin(component)
                mainSetting.isAdministrator = false
            }
        }
        val intentSuperTouchService = Intent(this, SuperTouchService::class.java)
        sw_active_touch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(this)) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        startActivityForResult(intent, 125)
                    } else {
                        startService(intentSuperTouchService)
                    }
                } else {
                    startService(intentSuperTouchService)
                }
            } else {
                stopService(intentSuperTouchService)
            }
            SuperTouchService.activity = this
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
                showDialogSelectIcon()
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
            //Grant device administration rights
            devicePolicyManager =
                getSystemService(Service.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            component = ComponentName(this, AdminReceiver::class.java)
            val isActive = devicePolicyManager.isAdminActive(componentName)

            if (!isActive) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    resources.getString(R.string.message_permission_device_admin)
                )
                startActivityForResult(intent, Companion.REQUEST_CODE)
            }
            dialogChangeAdministration.dismiss()
        }
        btnNoConfirmAdministration.setOnClickListener {
            sw_device_admin_permistion.isChecked = false
            dialogChangeAdministration.dismiss()
        }
        dialogChangeAdministration.show()

    }

    private fun checkServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    private fun getBackground(color: Int): GradientDrawable {
        val shape = GradientDrawable()
        shape.cornerRadius = 15.0f
        shape.setColor(color)
        return shape
    }

    override fun onDestroy() {
        super.onDestroy()
        HawkHelper.saveMainSetting(mainSetting)
    }

    override fun onResume() {
        super.onResume()
        mainSetting = HawkHelper.getMainSetting()
        img_preview_background.background = getBackground(mainSetting.backgroundColorTouchView)
    }

    private fun getAllImagePath() {
        try {
            val fileImage: Array<String> = assetManager.list("image") as Array<String>
            for (path in fileImage) {
                pathBGs.add(path)
                Log.e("IMG", path)
            }
        } catch (e: IOException) {
            Log.e("IMG", e.toString())
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class ImagePath : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg p0: Unit?) {
            getAllImagePath()
        }

    }

    @SuppressLint("InflateParams")
    private fun showDialogSelectIcon() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_select_icon_touch, null)
        val rvIconTouch = view.findViewById<RecyclerView>(R.id.rv_icon_touch)
        builder.setView(view)
        val dialog = builder.create()

        iconAdapter = IconAdapter(pathBGs, this) {
            mainSetting.nameIcon = it
            HawkHelper.saveMainSetting(mainSetting)

            Glide.with(this)
                .asBitmap()
                .load(Uri.fromFile(File("//android_asset/image/$it")))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        img_preview_icon.setImageBitmap(resource)
                        SuperTouchService.btnFloatingButton?.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
            SuperTouchService.btnFloatingButton
            dialog.dismiss()
        }
        rvIconTouch.setHasFixedSize(true)
        rvIconTouch.layoutManager = GridLayoutManager(this, 3)
        rvIconTouch.adapter = iconAdapter
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermistion() {
      try {
          val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
          // if user granted access else ask for permission
          // Open Setting screen to ask for permisssion
          if (!notificationManager.isNotificationPolicyAccessGranted) {
              val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
              startActivityForResult(intent, ON_DO_NOT_DISTURB_CALLBACK_CODE)

          }else{
              mainSetting.isRingMode = true
              HawkHelper.saveMainSetting(mainSetting)

          }
      }catch (e:Exception){
      e.printStackTrace()
      }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            Log.e(Constant.TAG, "$resultCode \n $requestCode")
            if (resultCode == Activity.RESULT_OK) {
                Log.e(Constant.TAG, " success $resultCode \n $requestCode")
                mainSetting.isAdministrator = true
                sw_device_admin_permistion.isChecked = true
            } else {
                Log.e(Constant.TAG, " fail $resultCode \n $requestCode")
                mainSetting.isAdministrator = false
                sw_device_admin_permistion.isChecked = false
            }
        }
        if (requestCode==ON_DO_NOT_DISTURB_CALLBACK_CODE){
            this.requestPermistion()
        }
            super.onActivityResult(requestCode, resultCode, data)

    }

}
