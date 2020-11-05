package com.cq.wechatworkassist

import android.content.Context
import android.util.Log

object AppConstants {
    var ip: String? = null

    fun init(context: Context) {
        ip = NetworkUtil.getSelfIp(context)
        Log.d("cuiqing", "init ip $ip")
    }
}