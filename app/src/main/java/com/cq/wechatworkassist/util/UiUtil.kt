package com.cq.wechatworkassist.util

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.text.Selection
import android.text.Spannable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

/**
 * Created by cuiqing on 2015/5/22.
 */
object UiUtil {
    fun showToast(context: Context?, resId: Int, duration: Int) {
        Toast.makeText(context, resId, duration).show()
    }

    fun showToast(
        context: Context?,
        text: String?,
        duration: Int
    ) {
        Toast.makeText(context, text, duration).show()
    }

    @JvmOverloads
    fun showToast(
        context: Context?,
        text: String?,
        gravity: Int,
        xOffset: Int,
        yOffset: Int,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        val toast = Toast.makeText(context, text, duration)
        toast.setGravity(gravity, xOffset, yOffset)
        toast.show()
    }

    fun showCenterToast(
        context: Context?,
        text: String?,
        duration: Int
    ) {
        showToast(context, text, Gravity.CENTER, 0, 0, duration)
    }

    fun showCenterToastShort(context: Context, resID: Int) {
        showCenterToast(context, context.resources.getString(resID), Toast.LENGTH_SHORT)
    }

    fun showCenterToastShort(context: Context?, msg: String?) {
        showCenterToast(context, msg, Toast.LENGTH_SHORT)
    }

    /**
     * 转换dp为px
     */
    fun dp2px(context: Context?, dp: Int): Int {
        return if (context == null) 0 else (context.resources.displayMetrics.density * dp).toInt()
    }

    /**
     * 转换dp为px
     */
    fun dp2px(context: Context, dp: Float): Int {
        return (context.resources.displayMetrics.density * dp).toInt()
    }

    fun px2dp(context: Context, pxValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        return Math.round(pxValue / scale)
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(context: Context): Int {
        val orientation = context.resources.configuration.orientation
        val width: Int
        width = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            context.resources.displayMetrics.heightPixels
        } else {
            context.resources.displayMetrics.widthPixels
        }
        return width
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(context: Context): Int {
        val orientation = context.resources.configuration.orientation
        val height: Int
        height = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            context.resources.displayMetrics.widthPixels
        } else {
            context.resources.displayMetrics.heightPixels
        }
        return height
    }

    /**
     * 获取虚拟按键的高度
     */
    fun getVirtualBarHeight(context: Context): Int {
        var vh = 0
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        try {
            val c = Class.forName("android.view.Display")
            val method =
                c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            vh = dm.heightPixels - display.height
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vh
    }

    /**
     * 是否显示密码
     *
     * @param tv   需要切换显示的TextView
     * @param show 是否显示
     */
    fun showPassword(tv: TextView, show: Boolean) {
        if (show) {
            tv.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            tv.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        tv.postInvalidate()
        //切换后将EditText光标置于末尾
        val charSequence = tv.text
        if (charSequence != null) {
            val spanText = charSequence as Spannable
            Selection.setSelection(spanText, charSequence.length)
        }
    }

    /**
     * 展现通知，目前所有通知使用统一的图标
     *
     * @param title      通知的title
     * @param content    通知的内容
     * @param tickerText 通知闪现的时候的文本
     */
    fun showNotification(
        context: Context?,
        notifyId: Int,
        title: String?,
        content: CharSequence?,
        tickerText: String?,
        intent: Intent?
    ) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setSmallIcon(R.drawable.ic_launcher);
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
//        builder.setContentTitle(title == null ? "" : title);
//        builder.setTicker(tickerText == null ? "" : tickerText);
//        builder.setContentText(content == null ? "" : content);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        Notification notification = builder.build();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(notifyId, notification);

//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_backup);
//        contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
//        contentView.setTextViewText(R.id.title, title);
//        contentView.setTextViewText(R.id.text, content);
//        builder.setContentTitle(title)
//                .setContentText(content)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentIntent(pendingIntent)
//                .setTicker(tickerText)
//                .setContent(contentView);
//        Notification notification = builder.build();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(notifyId, notification);
    }

    /**
     * 清除通知
     */
    fun clearNotify(context: Context, notifyId: Int) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notifyId)
    }

    /**
     * 清除所有通知
     */
    fun clearAllNotify(context: Context) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
    }
}