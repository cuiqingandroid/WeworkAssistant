package com.cq.wechatworkassist.ui.widget

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cq.wechatworkassist.R
import kotlin.math.abs

/**
 *
 */
class RecordScreenView(mContext: Context) : LinearLayout(mContext) {
    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var mLastDownX = 0f
    private var mLastDownY = 0f
    private var mIsMoving = false
    private var mTouchSlop = 0f
    private var mTvContent: TextView? = null
    private var mImageView: ImageView? = null
    fun setText(text: String?) {
        mTvContent!!.text = text
    }

    fun setOnImageClick(listener: OnClickListener?) {
        mImageView!!.setOnClickListener(listener)
    }

    fun setImageRes(resId: Int) {
        mImageView!!.setImageResource(resId)
    }

    init {
        val view = View.inflate(context, R.layout.float_controll, this)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        mTvContent = view.findViewById(R.id.phone)
        mImageView = view.findViewById(R.id.control)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
//        Log.d("cuiqing", "onInterceptTouchEvent ${event.action} $mIsMoving")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastDownX = event.rawX
                mLastDownY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mIsMoving && isTouchSlop(event)) {
                    mIsMoving = true
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsMoving = false
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        Log.d("cuiqing", "onTouchEvent ${event.action} $mIsMoving")
//      return super.onTouchEvent(event);
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsMoving = false
                mLastDownX = event.rawX
                mLastDownY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mIsMoving = true
                val params = layoutParams as WindowManager.LayoutParams
                params.x = params.x + (event.rawX - mLastDownX).toInt()
                params.y = params.y + (event.rawY - mLastDownY).toInt()
//                Log.d("cuiqing", " mIsMoving ${event.action} curr x:${params.x} y:${params.y} delx:${(event.rawX - mLastDownX)} dely:${ (event.rawY - mLastDownY)}")
                //刷新悬浮窗的位
                mWindowManager.updateViewLayout(this@RecordScreenView, params)
                mLastDownX = event.rawX
                mLastDownY = event.rawY
                return true
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsMoving = false
            }
        }
        return super.onTouchEvent(event)
    }


    /**
     * 判断是否滑动足够距离
     *
     * @param event
     * @return
     */
    private fun isTouchSlop(event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY
        return abs(x - mLastDownX) > mTouchSlop || abs(y - mLastDownY) > mTouchSlop
    }


}