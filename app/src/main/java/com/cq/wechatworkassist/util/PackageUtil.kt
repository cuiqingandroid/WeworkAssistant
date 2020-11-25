package com.cq.wechatworkassist.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object PackageUtil {
    /**
     * 获取应用包名
     */
    fun getVersion(context: Context, packageName: String): String? {
        return try {
            val manager: PackageManager = context.packageManager
            val info: PackageInfo = manager.getPackageInfo(packageName, 0)
            info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}