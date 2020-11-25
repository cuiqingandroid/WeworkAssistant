package com.cq.wechatworkassist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log

/**
 * 辅助功能/无障碍相关工具
 */
object AccessibilityUtil {
    private val TAG = AccessibilityUtil::class.java.simpleName

    /**
     * 检查系统设置，并显示设置对话框
     *
     * @param service 辅助服务
     */
    fun checkSetting(cxt: Context?, service: Class<*>): Boolean {
        if (isSettingOpen(service, cxt)) return true
        AlertDialog.Builder(cxt)
            .setTitle(R.string.aby_setting_title)
            .setMessage("找到【" + cxt?.getString(R.string.aby_label) + "】并开启")
            .setPositiveButton(
                R.string.common_ok
            ) { dialog: DialogInterface?, which: Int ->
                jumpToSetting(cxt)
            }
            .setCancelable(false)
            .show()
        return false
    }

    /**
     * 检查系统设置：是否开启辅助服务
     *
     * @param service 辅助服务
     */
    fun isSettingOpen(service: Class<*>, cxt: Context?): Boolean {
        try {
            val enable = Settings.Secure.getInt(
                cxt?.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                0
            )
            if (enable != 1) return false
            val services = Settings.Secure.getString(
                cxt?.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (!TextUtils.isEmpty(services)) {
                val split = SimpleStringSplitter(':')
                split.setString(services)
                while (split.hasNext()) { // 遍历所有已开启的辅助服务名
                    if (split.next().equals(
                            cxt?.packageName + "/" + service.name,
                            ignoreCase = true
                        )
                    ) return true
                }
            }
        } catch (e: Throwable) { //若出现异常，则说明该手机设置被厂商篡改了,需要适配
            Log.e(TAG, "isSettingOpen: " + e.message)
        }
        return false
    }

    /**
     * 跳转到系统设置：开启辅助服务
     */
    fun jumpToSetting(cxt: Context?) {
        try {
            cxt?.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (e: Throwable) { //若出现异常，则说明该手机设置被厂商篡改了,需要适配
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                cxt?.startActivity(intent)
            } catch (e2: Throwable) {
                Log.e(TAG, "jumpToSetting: " + e2.message)
            }
        }
    }

    /**
     * 唤醒点亮和解锁屏幕(60s)
     */
    fun wakeUpScreen(context: Context) {
        try {
            //唤醒点亮屏幕
            val pm =
                context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm != null && pm.isScreenOn) {
                @SuppressLint("InvalidWakeLockTag") val wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
                    "wakeUpScreen"
                )
                wl.acquire(60000) // 60s后释放锁
            }

            //解锁屏幕
            val km =
                context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (km != null && km.inKeyguardRestrictedInputMode()) {
                val kl = km.newKeyguardLock("unLock")
                kl.disableKeyguard()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "wakeUpScreen: " + e.message)
        }
    }
}