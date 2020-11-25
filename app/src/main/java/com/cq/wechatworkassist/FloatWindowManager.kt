package com.cq.wechatworkassist

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.cq.wechatworkassist.task.TaskManager
import com.cq.wechatworkassist.ui.widget.FloatControllerView
import com.cq.wechatworkassist.util.UiUtil

/**
 */
object FloatWindowManager {
    private var mBallView: FloatControllerView? = null
    fun addBallView(context: Context) {
        if (mBallView == null) {
            val windowManager =
                getWindowManager(context)
            val screenWidth = windowManager!!.defaultDisplay.width
            val screenHeight = windowManager.defaultDisplay.height
            mBallView =
                FloatControllerView(context)
            val params =
                WindowManager.LayoutParams()
            params.x = UiUtil.dp2px(context, 15)
            params.y = screenHeight - UiUtil.dp2px(context, 200)
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.START or Gravity.TOP

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            params.format = PixelFormat.RGBA_8888
            params.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            windowManager.addView(mBallView, params)
            if (TaskManager.isRunning) {
                setImageRes(R.drawable.ic_pause)
            } else {
                setImageRes(R.drawable.ic_start)
            }
            mBallView?.setOnImageClick(View.OnClickListener {
                if (TaskManager.isRunning) {
                    setImageRes(R.drawable.ic_start)
                    TaskManager.stop()
                } else {
                    if (TaskManager.start()) {
                        setImageRes(R.drawable.ic_pause)
                    } else {
                        Toast.makeText(context, "没有可执行的任务，点击菜单添加任务", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    fun isShow(): Boolean {
        return mBallView?.isShown == true
    }

    fun setPhone(phone: String?) {
        mBallView?.setText(phone)
    }
    fun setText(content: String?) {
        mBallView?.setText(content)
    }

    fun setImageOnClick(listener: View.OnClickListener) {
        mBallView?.setOnImageClick(listener)
    }

    fun setImageRes(resId: Int) {
        mBallView?.setImageRes(resId)
    }

    fun removeBallView(context: Context) {
        if (mBallView != null) {
            val windowManager =
                getWindowManager(context)
            windowManager.removeView(mBallView)
            mBallView = null
        }
    }

    private fun getWindowManager(context: Context): WindowManager {
        return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}