package com.cq.wechatworkassist.util

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


fun startWechatWorkMain() {
//        val intent = Intent(Intent.ACTION_MAIN)
//        intent.component = ComponentName(
//            "com.tencent.wework",
//            "com.tencent.wework.launch.LaunchSplashActivity"
//        )
//        intent.addCategory(Intent.CATEGORY_LAUNCHER)
////        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
    execRootCommand("am start -n com.tencent.wework/com.tencent.wework.launch.LaunchSplashActivity")
}

/**
 * 判断键盘是否可见
 */
fun isKeyboardOpen() : Boolean{
    val ret = execRootCommand("dumpsys input_method |grep mInputShown=true")
    return !ret.isNullOrBlank()
}

/**
 * 获取当前顶部activity
 */
fun rootGetActivity(): String? {
    val ret = execRootCommand("dumpsys activity top | grep ACTIVITY")
    if (ret != null && ret != "") {
        val items = ret.trim().split("\n")
        val arr =items[items.size -1].split(" ")
        if (arr.size > 1) {
            return arr[1].replace("/", "")
        }
    }
    return null
}

fun inputRootKeyevent(keyevent: Int): String? {
    val script = "input keyevent $keyevent"
    return execRootCommand(script)
}

fun execRootCommand(command: String): String? {
    val ex = Runtime.getRuntime()
    val cmdBecomeSu = "/system/xbin/su -"
    try {
        val runsum = ex.exec(cmdBecomeSu)
        var exitVal = 0
        val out =
            OutputStreamWriter(runsum.outputStream)
        val `in` =
            BufferedReader(InputStreamReader(runsum.inputStream))
        out.write(command)
        out.write("\n")
        out.flush()
        out.write("exit\n")
        out.flush()
        out.close()
        var s = ""
        var line: String?
        while (`in`.readLine().also { line = it } != null) {
            line?.trimIndent()
        }
        `in`.close()
        exitVal = runsum.waitFor()
        if (exitVal == 0) {
            Log.e("Debug", "Successfully to shell")
            return s
        }
    } catch (e: Exception) {
        Log.e("Debug", "Fails to shell")
    }
    return null
}