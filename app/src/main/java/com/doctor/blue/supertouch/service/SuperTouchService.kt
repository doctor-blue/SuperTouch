package com.doctor.blue.supertouch.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Vibrator
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.activities.MainActivity
import com.doctor.blue.supertouch.database.SuperTouchDatabase
import com.doctor.blue.supertouch.event.TouchEvent
import com.doctor.blue.supertouch.model.HawkHelper
import java.util.*

class SuperTouchService : Service() {
    private var mwindowManager: WindowManager? = null
    private var mFloatingButton: View? = null
    private var isFloatingViewAttached: Boolean? = null
    private lateinit var btnFloatingButton: ImageView
    private var isFloatingButtonShow: Boolean = false
    private val mRunnable = Runnable { setAnimationFloatingButton(true) }
    private var mainMenuSetting = HawkHelper.getMainMenuSetting()
    private var isMainMenu=true
    private var isControlMenu=false
    private lateinit var touchView: View
    private lateinit var imgItemCustom11: ImageView
    private lateinit var txtItemCustom11: TextView
    private lateinit var imgItemCustom12: ImageView
    private lateinit var txtItemCustom12: TextView
    private lateinit var imgItemCustom13: ImageView
    private lateinit var txtItemCustom13: TextView
    private lateinit var imgItemCustom21: ImageView
    private lateinit var txtItemCustom21: TextView
    private lateinit var imgItemCustom22: ImageView
    private lateinit var txtItemCustom22: TextView
    private lateinit var imgItemCustom23: ImageView
    private lateinit var txtItemCustom23: TextView
    private lateinit var imgItemCustom31: ImageView
    private lateinit var txtItemCustom31: TextView
    private lateinit var imgItemCustom32: ImageView
    private lateinit var txtItemCustom32: TextView
    private lateinit var imgItemCustom33: ImageView
    private lateinit var txtItemCustom33: TextView

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var activity:Activity
    }



    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        touchView=LayoutInflater.from(this).inflate(R.layout.layout_item_touch,null)
        innitFloatingButton()
        innitView()
        innitEventView()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_NOT_STICKY
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setFloatingButtonEvent(layoutParams: WindowManager.LayoutParams) {
        val display = mwindowManager!!.defaultDisplay
        val screenWith = display.width
        mFloatingButton?.setOnTouchListener(object : View.OnTouchListener {
            private var startime: Long? = null
            private var initX: Int? = null
            private var initY: Int? = null
            private var initTouchX: Float? = null
            private var initTouchY: Float? = null
            private var isLongClick: Boolean? = null
            private val timeTouchLongClick = 5000
            private var isClick = true

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when (event?.action) {

                    MotionEvent.ACTION_DOWN -> {
                        isLongClick = true
                        initTouchX = event.rawX
                        initTouchY = event.rawY
                        initX = layoutParams.x
                        initY = layoutParams.y
                        //set startime
                        startime = System.currentTimeMillis()


                    }
                    MotionEvent.ACTION_MOVE -> {

                        layoutParams.x = initX!! + (event.rawX - this.initTouchX!!).toInt()
                        layoutParams.y = initY!! + (event.rawY - this.initTouchY!!).toInt()
                        if (!isFloatingButtonShow) {
                            mwindowManager?.updateViewLayout(mFloatingButton, layoutParams)
                            val handler = Handler()
                            val t = Timer()
                            t.schedule(object : TimerTask() {
                                override fun run() {
                                    handler.post {
                                        //DO SOME ACTIONS HERE , THIS ACTIONS WILL WILL EXECUTE AFTER 5 SECONDS...
                                        setAnimationFloatingButton(true)
                                    }
                                }
                            }, 5000)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        setAnimationFloatingButton(false)
                        isLongClick = false
                        val xDiff = (event.rawX - initTouchX?.toInt()!!)
                        val yDiff = (event.rawY - initTouchY?.toInt()!!)

                        if (!isFloatingButtonShow) {
                            if (layoutParams.x < screenWith / 2) {
                                while (layoutParams.x > 0) {
                                    layoutParams.x -= 10
                                    mwindowManager!!.updateViewLayout(mFloatingButton, layoutParams)
                                }
                            } else {
                                while (layoutParams.x < screenWith) {
                                    layoutParams.x += 10
                                    mwindowManager!!.updateViewLayout(mFloatingButton, layoutParams)
                                }
                            }
                        }
                        //The check for xDiff <10 && yDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        val timeDelay: Long = System.currentTimeMillis() - startime!!
                        if (xDiff < 10 && yDiff < 10 && timeDelay < 1000) {
                            if (isClick) {
                                showMainMenu()
                                isClick=false
                            }
                            else{
                                showTouchView()
                            }

                        }
                        val handler = Handler()
                        handler.postDelayed({
                            if (isLongClick!!) {
                                if (timeDelay > 1000L) {
                                    val vibrator: Vibrator =
                                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    vibrator.vibrate(100L)
                                }
                            }
                        }, timeTouchLongClick.toLong())

                        return true

                    }
                }
                return false
            }

        })
    }

    private fun showMainMenu() {
        innitMainMenu()

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val windowManagerMainMenu = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.CENTER
        layoutParams.x = 0
        layoutParams.y = 0

        //setbackgoundisSave()

        windowManagerMainMenu.addView(touchView, layoutParams)

        touchView.setOnTouchListener(object : View.OnTouchListener {

            var startime: Long? = null
            var initX: Int? = null
            var initY: Int? = null
            var initTouchX: Float? = null
            var initTouchY: Float? = null
            var islongclick: Boolean? = null
            var isclick = true

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        islongclick = true
                        initTouchX = event.rawX
                        initTouchY = event.rawY
                        initX = layoutParams.x
                        initY = layoutParams.y
                        //set startime
                        startime = System.currentTimeMillis()

                    }
                    MotionEvent.ACTION_MOVE -> {

                    }
                    MotionEvent.ACTION_UP -> {
                        islongclick = false
                        val xDiff = (event.rawX - initTouchX?.toInt()!!)
                        val yDiff = (event.rawY - initTouchY?.toInt()!!)
                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        val timeDelay: Long = System.currentTimeMillis() - startime!!
                        if (xDiff < 10 && yDiff < 10 && timeDelay < 1000) {
                            isclick = if (isclick) {
                                // return floating button
                                hideTouchView()
                                false
                            }else {
                                showTouchView()
                                true
                            }
                        }
                        return true
                    }
                }
                return false
            }

        })
    }
    private fun hideTouchView(){
        touchView.visibility = View.GONE
        mFloatingButton?.visibility = View.VISIBLE
    }
    private fun showTouchView(){
        touchView.visibility=View.VISIBLE
        mFloatingButton?.visibility = View.GONE
    }

    private fun innitEventView(){
        imgItemCustom11.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom11
            when {
                isMainMenu -> {TouchEvent.mainMenuEvent(mainMenuSetting.idItem11);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom12.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom12
            when {
                isMainMenu -> {TouchEvent.mainMenuEvent(mainMenuSetting.idItem12);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom13.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom13
            when {
                isMainMenu -> {TouchEvent.mainMenuEvent(mainMenuSetting.idItem13);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom21.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom21
            when {
                isMainMenu -> {TouchEvent.mainMenuEvent(mainMenuSetting.idItem21);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom22.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom22
            when {
                isMainMenu ->{ TouchEvent.mainMenuEvent(mainMenuSetting.idItem22);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom23.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom23
            when {
                isMainMenu ->{ TouchEvent.mainMenuEvent(mainMenuSetting.idItem23);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom31.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom31
            when {
                isMainMenu -> {TouchEvent.mainMenuEvent(mainMenuSetting.idItem31);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom32.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom32
            when {
                isMainMenu -> {TouchEvent.mainMenuEvent(mainMenuSetting.idItem32);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
        imgItemCustom33.setOnClickListener{
            TouchEvent.imgTouch=it as ImageView
            TouchEvent.txtNameTouch=txtItemCustom33
            when {
                isMainMenu -> {TouchEvent.mainMenuEvent(mainMenuSetting.idItem33);hideTouchView()}
                isControlMenu -> {

                }
                else -> {

                }
            }
        }
    }
    private fun innitView() {
        imgItemCustom11 = touchView.findViewById(R.id.img_item_custom_1_1)
        txtItemCustom11 = touchView.findViewById(R.id.txt_item_custom_1_1)
        imgItemCustom12 = touchView.findViewById(R.id.img_item_custom_1_2)
        txtItemCustom12 = touchView.findViewById(R.id.txt_item_custom_1_2)
        imgItemCustom13 = touchView.findViewById(R.id.img_item_custom_1_3)
        txtItemCustom13 = touchView.findViewById(R.id.txt_item_custom_1_3)
        imgItemCustom21 = touchView.findViewById(R.id.img_item_custom_2_1)
        txtItemCustom21 = touchView.findViewById(R.id.txt_item_custom_2_1)
        imgItemCustom22 = touchView.findViewById(R.id.img_item_custom_2_2)
        txtItemCustom22 = touchView.findViewById(R.id.txt_item_custom_2_2)
        imgItemCustom23 = touchView.findViewById(R.id.img_item_custom_2_3)
        txtItemCustom23 = touchView.findViewById(R.id.txt_item_custom_2_3)
        imgItemCustom31 = touchView.findViewById(R.id.img_item_custom_3_1)
        txtItemCustom31 = touchView.findViewById(R.id.txt_item_custom_3_1)
        imgItemCustom32 = touchView.findViewById(R.id.img_item_custom_3_2)
        txtItemCustom32 = touchView.findViewById(R.id.txt_item_custom_3_2)
        imgItemCustom33 = touchView.findViewById(R.id.img_item_custom_3_3)
        txtItemCustom33 = touchView.findViewById(R.id.txt_item_custom_3_3)
        TouchEvent.context=this
        TouchEvent.activity= activity
    }

    private fun innitMainMenu() {
        val getIconId: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).iconItem }
        val getNameItemId: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).nameItem }

        imgItemCustom11.setImageResource(getIconId(mainMenuSetting.idItem11))
        txtItemCustom11.text = resources.getText(getNameItemId(mainMenuSetting.idItem11))

        imgItemCustom12.setImageResource(getIconId(mainMenuSetting.idItem12))
        txtItemCustom12.text = resources.getText(getNameItemId(mainMenuSetting.idItem12))

        imgItemCustom13.setImageResource(getIconId(mainMenuSetting.idItem13))
        txtItemCustom13.text = resources.getText(getNameItemId(mainMenuSetting.idItem13))

        imgItemCustom21.setImageResource(getIconId(mainMenuSetting.idItem21))
        txtItemCustom21.text = resources.getText(getNameItemId(mainMenuSetting.idItem21))

        imgItemCustom22.setImageResource(getIconId(mainMenuSetting.idItem22))
        txtItemCustom22.text = resources.getText(getNameItemId(mainMenuSetting.idItem22))

        imgItemCustom23.setImageResource(getIconId(mainMenuSetting.idItem23))
        txtItemCustom23.text = resources.getText(getNameItemId(mainMenuSetting.idItem23))

        imgItemCustom31.setImageResource(getIconId(mainMenuSetting.idItem31))
        txtItemCustom31.text = resources.getText(getNameItemId(mainMenuSetting.idItem31))

        imgItemCustom32.setImageResource(getIconId(mainMenuSetting.idItem32))
        txtItemCustom32.text = resources.getText(getNameItemId(mainMenuSetting.idItem32))

        imgItemCustom33.setImageResource(getIconId(mainMenuSetting.idItem33))
        txtItemCustom33.text = resources.getText(getNameItemId(mainMenuSetting.idItem33))


    }

    @SuppressLint("InflateParams")
    private fun innitFloatingButton() {
        mFloatingButton = LayoutInflater.from(this).inflate(R.layout.layout_floating_view, null)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        mwindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 0
        layoutParams.y = 0

        btnFloatingButton = mFloatingButton!!.findViewById(R.id.btn_floating_button)
        //setIconTouch()
        mwindowManager!!.addView(mFloatingButton, layoutParams)
        isFloatingViewAttached = true

        setFloatingButtonEvent(layoutParams)
    }

    private fun setAnimationFloatingButton(touch: Boolean) {
        val mHandler: Handler? = Handler()
        if (touch) {
            val alpha = AlphaAnimation(0.5f, 0.5f) // change values as you want
            alpha.duration = 0 // Make animation instant
            alpha.fillAfter = true // Tell it to persist after the animation ends
            btnFloatingButton.startAnimation(alpha)
            mFloatingButton?.background?.alpha = 40
            mHandler?.postDelayed(mRunnable, 5000)
        } else {
            val alpha = AlphaAnimation(1f, 1f) // change values as you want
            alpha.duration = 0 // Make animation instant
            alpha.fillAfter = true // Tell it to persist after the animation ends
            btnFloatingButton.startAnimation(alpha)
            mFloatingButton?.background?.alpha = 1
            mHandler?.postDelayed(mRunnable, 5000)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mFloatingButton?.let {
            mwindowManager?.removeView(mFloatingButton)
            isFloatingViewAttached = false
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}