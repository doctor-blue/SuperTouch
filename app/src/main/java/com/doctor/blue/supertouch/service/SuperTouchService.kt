package com.doctor.blue.supertouch.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Vibrator
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.activities.ListApplicationActivity
import com.doctor.blue.supertouch.database.SuperTouchDatabase
import com.doctor.blue.supertouch.event.TouchEvent
import com.doctor.blue.supertouch.keys.Constant
import com.doctor.blue.supertouch.model.HawkHelper
import com.doctor.blue.supertouch.model.MainSetting
import com.doctor.blue.supertouch.views.TouchView
import kotlinx.android.synthetic.main.touch_view.view.*
import java.io.File
import java.util.*

class SuperTouchService : Service() {
    private var mwindowManager: WindowManager? = null
    private var mFloatingButton: View? = null
    private var isFloatingViewAttached: Boolean? = null
    private var isFloatingButtonShow: Boolean = false
    private val mRunnable = Runnable { setAnimationFloatingButton(true) }
    private var mainMenuSetting = HawkHelper.getMainMenuSetting()
    private lateinit var mainSetting: MainSetting
    private var itemApplication = HawkHelper.getItemApplication()
    private var isMainMenu = true
    private var isControlMenu = false
    private lateinit var layoutTouch: View
    private lateinit var touchView: TouchView

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var activity: Activity
        @SuppressLint("StaticFieldLeak")
        var btnFloatingButton: ImageView? = null
    }


    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        layoutTouch = LayoutInflater.from(this).inflate(R.layout.layout_touch, null)
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
                                isClick = false
                            } else {
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

        windowManagerMainMenu.addView(layoutTouch, layoutParams)

        layoutTouch.setOnTouchListener(object : View.OnTouchListener {

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
                            } else {
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

    private fun hideTouchView() {
        layoutTouch.visibility = View.GONE
        mFloatingButton?.visibility = View.VISIBLE
    }

    private fun showTouchView() {
        layoutTouch.visibility = View.VISIBLE
        mFloatingButton?.visibility = View.GONE
        innitMainMenu()
    }

    private fun innitEventView() {
        touchView.item11OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem11
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()

                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName11)

                }
            }
        }
        touchView.item12OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem12
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()
                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName12)

                }
            }
        }
        touchView.item13OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem13
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()
                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName13)
                }
            }
        }
        touchView.item21OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem21
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()
                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName21)
                }
            }
        }
        touchView.item23OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem23
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()
                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName23)
                }
            }
        }
        touchView.item31OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem31
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()
                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName31)
                }
            }
        }
        touchView.item32OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem32
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()
                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName32)
                }
            }
        }
        touchView.item33OnClick = { imageView, textView ->
            TouchEvent.imgTouch = imageView
            TouchEvent.txtNameTouch = textView
            when {
                isMainMenu -> {
                    val id = mainMenuSetting.idItem33
                    if (id != Constant.idApplicationItem) {
                        TouchEvent.mainMenuEvent(id)
                        hideTouchView()
                    } else
                        innitApplicationMenu()
                }
                isControlMenu -> {

                }
                else -> {
                    starApplication(itemApplication.packageName33)
                }
            }
        }

        touchView.img_item_touch_1_1.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp11)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
        touchView.img_item_touch_1_2.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp12)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
        touchView.img_item_touch_1_3.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp13)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
        touchView.img_item_touch_2_1.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp21)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
        touchView.img_item_touch_2_3.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp23)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
        touchView.img_item_touch_3_1.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp31)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
        touchView.img_item_touch_3_2.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp32)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
        touchView.img_item_touch_3_3.setOnLongClickListener {
            if (!isMainMenu && !isControlMenu) {
                selectApplication(Constant.itemApp33)
                hideTouchView()
            }
            return@setOnLongClickListener true
        }
    }

    private fun selectApplication(itemApp: String) {
        val intent = Intent(TouchEvent.activity, ListApplicationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constant.PICK_APP, itemApp)
        TouchEvent.activity.startActivity(intent)
    }

    private fun innitView() {
        mainSetting = HawkHelper.getMainSetting()
        touchView = layoutTouch.findViewById(R.id.touch_view_layout_touch)
        touchView.setBackgroundColorTouchView(mainSetting.backgroundColorTouchView)
        TouchEvent.context = this
        TouchEvent.activity = activity
    }

    private fun innitMainMenu() {
        mainMenuSetting = HawkHelper.getMainMenuSetting()
        mainSetting = HawkHelper.getMainSetting()
        touchView.setBackgroundColorTouchView(mainSetting.backgroundColorTouchView)

        val getIconId: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).iconItem }
        val getTitleItem: (String) -> Int = { id -> SuperTouchDatabase.getItemTouch(id).nameItem }

        touchView.setIconItem(
            getIconId(mainMenuSetting.idItem11),
            getIconId(mainMenuSetting.idItem12),
            getIconId(mainMenuSetting.idItem13),
            getIconId(mainMenuSetting.idItem21),
            getIconId(mainMenuSetting.idItem23),
            getIconId(mainMenuSetting.idItem31),
            getIconId(mainMenuSetting.idItem32),
            getIconId(mainMenuSetting.idItem33)
        )
        touchView.setTextItem(
            getTitleItem(mainMenuSetting.idItem11),
            getTitleItem(mainMenuSetting.idItem12),
            getTitleItem(mainMenuSetting.idItem13),
            getTitleItem(mainMenuSetting.idItem21),
            getTitleItem(mainMenuSetting.idItem23),
            getTitleItem(mainMenuSetting.idItem31),
            getTitleItem(mainMenuSetting.idItem32),
            getTitleItem(mainMenuSetting.idItem33)
        )

        isControlMenu = false
        isMainMenu = true


    }

    private fun innitApplicationMenu() {
        itemApplication = HawkHelper.getItemApplication()
        mainSetting = HawkHelper.getMainSetting()

        val setInfomationItem: (ImageView, String) -> Unit = { imgItem, packageName ->
            if (packageName.isEmpty())
                imgItem.setImageResource(R.drawable.ic_add)
            else {
                val icon = this.packageManager?.getApplicationIcon(packageName)
                imgItem.setImageDrawable(icon)
            }

        }

        touchView.setTextItem(
            R.string.empty,
            R.string.empty,
            R.string.empty,
            R.string.empty,
            R.string.empty,
            R.string.empty,
            R.string.empty,
            R.string.empty
        )

        setInfomationItem(touchView.img_item_touch_1_1, itemApplication.packageName11)
        setInfomationItem(touchView.img_item_touch_1_2, itemApplication.packageName12)
        setInfomationItem(touchView.img_item_touch_1_3, itemApplication.packageName13)
        setInfomationItem(touchView.img_item_touch_2_1, itemApplication.packageName21)
        setInfomationItem(touchView.img_item_touch_2_3, itemApplication.packageName23)
        setInfomationItem(touchView.img_item_touch_3_1, itemApplication.packageName31)
        setInfomationItem(touchView.img_item_touch_3_2, itemApplication.packageName32)
        setInfomationItem(touchView.img_item_touch_3_3, itemApplication.packageName33)

        isMainMenu = false
        isControlMenu = false


    }

    @SuppressLint("InflateParams")
    private fun innitFloatingButton() {
        mainSetting = HawkHelper.getMainSetting()
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
        //set Icon Touch
        Glide.with(this)
            .load(Uri.fromFile(File("//android_asset/image/" + mainSetting.nameIcon)))
            .apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(64, 64)
            )
            .into(btnFloatingButton!!)
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
            btnFloatingButton!!.startAnimation(alpha)
            mFloatingButton?.background?.alpha = 40
            mHandler?.postDelayed(mRunnable, 5000)
        } else {
            val alpha = AlphaAnimation(1f, 1f) // change values as you want
            alpha.duration = 0 // Make animation instant
            alpha.fillAfter = true // Tell it to persist after the animation ends
            btnFloatingButton!!.startAnimation(alpha)
            mFloatingButton?.background?.alpha = 1
            mHandler?.postDelayed(mRunnable, 5000)
        }
    }

    private fun starApplication(packageName: String) {
        if (packageName.isNotEmpty()) {
            hideTouchView()
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
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