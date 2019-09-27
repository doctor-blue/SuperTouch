package com.doctor.blue.supertouch.model

import android.annotation.SuppressLint
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
import com.doctor.blue.supertouch.R
import java.util.*

class SuperTouchService : Service() {
    private var mwindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    private var isFloatingViewAttached: Boolean? = null
    private lateinit var btnFloatingButton: ImageView
    private var isFloatingButtonShow: Boolean = false
    private val mRunnable = Runnable { setAnimationFloatingButton(true) }

    //private var fullLayout: ViewGroup? = null
    override fun onCreate() {
        super.onCreate()
        innitFloatingButton()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setFloatingButtonEvent(layoutParams: WindowManager.LayoutParams) {
        val display = mwindowManager!!.defaultDisplay
        val screenWith = display.width
        mFloatingView?.setOnTouchListener(object : View.OnTouchListener {
            private var startime: Long? = null
            private var initX: Int? = null
            private var initY: Int? = null
            private var initTouchX: Float? = null
            private var initTouchY: Float? = null
            private var isLongClick: Boolean? = null
            private val timeTouchLongClick = 5000
            private var isClick = false

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
                            mwindowManager?.updateViewLayout(mFloatingView, layoutParams)
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
                                    mwindowManager!!.updateViewLayout(mFloatingView, layoutParams)
                                }
                            } else {
                                while (layoutParams.x < screenWith) {
                                    layoutParams.x += 10
                                    mwindowManager!!.updateViewLayout(mFloatingView, layoutParams)
                                }
                            }
                        }
                        //The check for xDiff <10 && yDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        val timeDelay: Long = System.currentTimeMillis() - startime!!
                        if (xDiff < 10 && yDiff < 10 && timeDelay < 1000) {
                            isClick = !isClick
                            if (isClick) {
                                //Showlayoutmain()
                            }

                        }
                        val handler = Handler()
                        handler.postDelayed({
                            if (isLongClick!!) {
                                if (timeDelay > 1000L) {
                                    val vibrator: Vibrator =
                                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    vibrator.vibrate(100L)
                                    // HideTouch3D()
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

    @SuppressLint("InflateParams")
    private fun innitFloatingButton() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_view, null)

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
        //trước khi addview set icon cho touch
        btnFloatingButton = mFloatingView!!.findViewById(R.id.btn_floating_button)
        //setIconTouch()
        mwindowManager!!.addView(mFloatingView, layoutParams)
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
            mFloatingView?.background?.alpha = 40
            mHandler?.postDelayed(mRunnable, 5000)
        } else {
            val alpha = AlphaAnimation(1f, 1f) // change values as you want
            alpha.duration = 0 // Make animation instant
            alpha.fillAfter = true // Tell it to persist after the animation ends
            btnFloatingButton.startAnimation(alpha)
            mFloatingView?.background?.alpha = 1
            mHandler?.postDelayed(mRunnable, 5000)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}