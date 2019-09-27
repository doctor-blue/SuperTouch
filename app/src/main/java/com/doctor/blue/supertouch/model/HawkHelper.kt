package com.doctor.blue.supertouch.model

import android.content.Context
import com.doctor.blue.supertouch.keys.Constant
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk

class HawkHelper {
    companion object{
        private val gson: Gson = Gson()
        fun init(context: Context){
            Hawk.init(context).build()
        }
        fun saveMainSetting(mainSetting: MainSetting){
            val data:String= gson.toJson(mainSetting)
            Hawk.put(Constant.MAIN_SETTING,data)
        }
        fun getMainSetting():MainSetting{
            val mainSetting:MainSetting
            val data:String= Hawk.get(Constant.MAIN_SETTING,"")
            if (data.isEmpty()){
                mainSetting= MainSetting()
                mainSetting.available=false
            }else{
                mainSetting= gson.fromJson(data,MainSetting::class.java)
                mainSetting.available=true
            }
            return mainSetting
        }
    }
}