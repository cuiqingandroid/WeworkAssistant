package com.cq.wechatworkassist

import okhttp3.*
import java.io.IOException

object NetApi {
    val okhttpclient = OkHttpClient().newBuilder().build()
    fun uploadStatus(phone: String, name: String?, status: String) {
        val httpUrl = HttpUrl.Builder().scheme("https").host("mapi.wandougongzhu.cn")
//                                        .addPathSegment("uploadStatus")
                                        .addQueryParameter("phone", phone)
            .addQueryParameter("name", name)
            .addQueryParameter("status", status)
            .addQueryParameter("ip", AppConstants.ip)
            .addQueryParameter("method", "Bot.qywxBotSync")
        val request = Request.Builder().url(httpUrl.build()).build()
        okhttpclient.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }

        })
    }
}