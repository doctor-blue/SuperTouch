package com.doctor.blue.supertouch.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.doctor.blue.supertouch.R
import kotlinx.android.synthetic.main.touch_view.view.*


class TouchView : RelativeLayout {
    private var root: View? = null
    private var isBorder:Boolean=true
    constructor(context: Context):super(context){
        inits(context,null)
    }
    constructor(context: Context,attrs:AttributeSet):super(context,attrs){
        inits(context,attrs)
    }
    constructor(context: Context, attrs: AttributeSet,  defStyleAttr:Int):super(context, attrs, defStyleAttr)

    @SuppressLint("Recycle")
    private fun inits(context: Context, attrs: AttributeSet?){
        root= View.inflate(context, R.layout.touch_view,this)
        attrs.let {
            val typedArray=context.obtainStyledAttributes(attrs,R.styleable.TouchView)
            isBorder=typedArray.getBoolean(R.styleable.TouchView_item_border,true)
            if (!isBorder){
                removeItemBorder()
            }
        }
    }
    fun removeItemBorder(){
        img_bg_item_1_1.setBackgroundColor(resources.getColor(android.R.color.transparent))
        img_bg_item_1_2.setBackgroundColor(resources.getColor(android.R.color.transparent))
        img_bg_item_1_3.setBackgroundColor(resources.getColor(android.R.color.transparent))
        img_bg_item_2_1.setBackgroundColor(resources.getColor(android.R.color.transparent))
        img_bg_item_2_3.setBackgroundColor(resources.getColor(android.R.color.transparent))
        img_bg_item_3_1.setBackgroundColor(resources.getColor(android.R.color.transparent))
        img_bg_item_3_2.setBackgroundColor(resources.getColor(android.R.color.transparent))
        img_bg_item_3_3.setBackgroundColor(resources.getColor(android.R.color.transparent))
        invalidate()
    }

    fun setIconItem(icon11:Int,icon12:Int,icon13:Int,icon21:Int,icon23:Int,icon31:Int,icon32:Int,icon33:Int){
        img_item_touch_1_1.setImageResource(icon11)
        img_item_touch_1_2.setImageResource(icon12)
        img_item_touch_1_3.setImageResource(icon13)
        img_item_touch_2_1.setImageResource(icon21)
        img_item_touch_2_3.setImageResource(icon23)
        img_item_touch_3_1.setImageResource(icon31)
        img_item_touch_3_2.setImageResource(icon32)
        img_item_touch_3_3.setImageResource(icon33)
    }

    fun setTextItem(text11:Int,text12:Int,text13:Int,text21:Int,text23:Int,text31:Int,text32:Int,text33:Int){
        txt_item_touch_1_1.text=resources.getString(text11)
        txt_item_touch_1_2.text=resources.getString(text12)
        txt_item_touch_1_3.text=resources.getString(text13)
        txt_item_touch_2_1.text=resources.getString(text21)
        txt_item_touch_2_3.text=resources.getString(text23)
        txt_item_touch_3_1.text=resources.getString(text31)
        txt_item_touch_3_2.text=resources.getString(text32)
        txt_item_touch_3_3.text=resources.getString(text33)
    }

    fun setBackgroundColorTouchView(color:Int){
        layout_list_touch_item.background=getBackground(color)
        invalidate()
    }

    private fun getBackground(color: Int): GradientDrawable {
        val shape = GradientDrawable()
        shape.cornerRadius = 15.0f
        shape.setColor(color)
        return shape
    }
}