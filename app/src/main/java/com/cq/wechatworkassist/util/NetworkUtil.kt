package com.cq.wechatworkassist.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.util.Log
import java.net.NetworkInterface
import java.net.SocketException


object NetworkUtil {

    @SuppressLint("MissingPermission")
    fun getSelfIp(context: Context): String {
        val wm = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress = wm.connectionInfo.ipAddress
        val ip = (ipAddress and 0xff).toString() + "." + (ipAddress shr 8 and 0xff) + "." + (ipAddress shr 16 and 0xff) + "." + (ipAddress shr 24 and 0xff)
        return ip
    }

    fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("NetworkUtil", ex.toString())
        }

        return ""
    }

}