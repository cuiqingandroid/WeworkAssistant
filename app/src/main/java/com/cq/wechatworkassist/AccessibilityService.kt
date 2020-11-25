package com.cq.wechatworkassist

import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.cq.wechatworkassist.task.*
import com.cq.wechatworkassist.util.RandUtil
import com.cq.wechatworkassist.wework.*


/**
 * 辅助服务自动安装APP，该服务在单独进程中允许
 */
class AccessibilityService : android.accessibilityservice.AccessibilityService() {


    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (!checkUi()) {
                removeMessages(0)
                sendEmptyMessageDelayed(0, RandUtil.randomInt(1500, 3000))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkUi() : Boolean{
        if (mRunningTask != null) {
//            if (isKeyboardOpen()) {
//                pressBack()
//                return false
//            }
//            mTopActivityName = tryGetActivity()
            if (rootInActiveWindow == null){
                inputTap(RandUtil.randomInt(700).toFloat(), RandUtil.randomInt(120).toFloat())
                mHandler.sendEmptyMessageDelayed(0, RandUtil.randomLong(500))
                return true
            }
            if (!isRootFullscreen()) {
                pressBack()
                return false
            }
            log("checkui ${mRunningTask?.phone} topActivityName:$mTopActivityName")
            // 检测企业微信是否开启
            if (mTopActivityName?.contains(WEWORK_PACKAGE) != true) {
                FloatWindowManager.setText("请打开企业微信")
//                startWechatWorkMain()
                mHandler.sendEmptyMessageDelayed(0, 10000)
//                mHandler.postDelayed(
//                    { if (ACTIVITY_CONTACT_SEND_VERIFY == mTopActivityName) {
//                        pressBack()
//                    } },
//                    Util.randomInt(1500,2500)
//                )

                return true
            }
            FloatWindowManager.setText("正在运行${mRunningTask?.phone}")
            if (ACTIVITY_MAIN == mTopActivityName) {
                // 判断加微信菜单是否显示
                if (findViewByText("加微信")) {
                    val rootNode = rootInActiveWindow
                    if (rootNode != null) {
                        val nodes =
                            rootNode.findAccessibilityNodeInfosByViewId(getViewIdAddWechat())
                        if (nodes != null && nodes.isNotEmpty()) {
                            nodes[1].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                    }
                } else {
                    findViewIdClick(getViewIdAdd())
                }
            }
            if (ACTIVITY_ADD_FRIEND == mTopActivityName) {
                val view = findViewIdFirst(getViewIdSearchPhone())
                if (view != null) {
                    tapViewById(getViewIdSearchPhone())
                }
            }
            if (ACTIVITY_FRIEND_ADD_MULTI == mTopActivityName) {
                findViewIdClick("com.tencent.wework:id/bs9")
            }
            if (ACTIVITY_ADD_FRIEND_SEARCH == mTopActivityName) {
                if (findViewByText(WEWORK_UI_TEXT_OPERATION_LIMIT)) {
                    val phone = mRunningTask!!.phone
                    val name = mRunningTask!!.name
                    mRunningTask = null
                    TaskManager.onTaskEnd(phone, name, STATUS_OPERATION_LIMIT)
                    return false
                }
                val unexists = findViewByText(WEWORK_UI_TEXT_USER_NOT_EXISTS)
                if (unexists) {
                    val phone = mRunningTask!!.phone
                    val name = mRunningTask!!.name
                    mRunningTask = null

                    pressBack()
                    mHandler.postDelayed(
                        { pressBack() },
                        2000
                    )
                    TaskManager.onTaskEnd(phone, name, STATUS_UNKNOWN_PHONE)
                    return false
                }

                val searchTextView = findViewIdFirst(getViewIdEdSearchPhone())
                if (searchTextView == null) {
                    pressBack()
                    return false
                }
                if (searchTextView.text.toString() == mRunningTask?.phone) {
                    // 点击搜索结果
                    tapViewById(getViewIdLayoutSearchPhoneResult())
                } else {
                    findViewIdSetText(getViewIdEdSearchPhone(), mRunningTask!!.phone)
                }
            }
            if (ACTIVITY_CONTACT_SEND_EXISTS == mTopActivityName) {
                // 已存在，上报已存在
                val phone = mRunningTask!!.phone
                val name = mRunningTask!!.name
                mRunningTask = null
                pressBack()
                mHandler.postDelayed(
                    { pressBack() },
                    2000
                )
                TaskManager.onTaskEnd(phone, name, STATUS_ALREADY_FRIEND)
            }
            if (ACTIVITY_FRIEND_COMPANY_DETAIL == mTopActivityName) {
                // 已存在，上报已存在
                val phone = mRunningTask!!.phone
                val name = mRunningTask!!.name
                mRunningTask = null
                pressBack()
                mHandler.postDelayed(
                    { pressBack() },
                    2000
                )
                TaskManager.onTaskEnd(phone, name, STATUS_WEWORK_FRIEND)
            }
            if (ACTIVITY_FRIEND_COMPANY == mTopActivityName) {
                // 已存在，上报已存在
                val phone = mRunningTask!!.phone
                val name = mRunningTask!!.name
                mRunningTask = null
                pressBack()
                mHandler.postDelayed(
                    { pressBack() },
                    2000
                )
//                uploadStatus(phone, name, "已经是企业微信好友")
                TaskManager.onTaskEnd(phone, name, STATUS_ALREADY_WEWORK_FRIEND)
            }
            if (ACTIVITY_CONTACT_INFO == mTopActivityName) {
                val hasAdd = checkViewsIdExists(getViewContactInfoAddBtn())
                mRunningTask!!.name = findViewIdText(getViewContactInfoName())
                val view = findViewIdFirst(getViewContactInfoPhone())
                if (!hasAdd || view == null){
                    pressBack()

                    return false
                }
                if (hasAdd) {
                    if (view.getChild(0).text.toString() == mRunningTask!!.phone) {
                        findViewIdClick(getViewContactInfoAddBtn())
                    } else {
                        pressBack()
                        mHandler.postDelayed({pressBack()}, RandUtil.randomInt(500, 1000))
                    }
                }
            }
            if (ACTIVITY_CONTACT_SEND_VERIFY == mTopActivityName) {
                if (companyName == "") {
                    companyName = findViewIdText(getViewSendVerifyCompany()) ?: ""
                }
                val welcomeText = findViewIdText(getViewSendVerifyWelcome())
                if (welcomeText == mRunningTask?.content) {
                    if (findViewIdClick(getViewSendVerifyButton())) {
                        val phone = mRunningTask!!.phone
                        val name = mRunningTask!!.name
                        mRunningTask = null
                        mHandler.postDelayed({pressBack()},
                            RandUtil.randomLong(500,1000))
                        mHandler.postDelayed({pressBack()},
                            RandUtil.randomLong(2000,3000))
                        // 上报成功
                        TaskManager.onTaskEnd(phone, name, STATUS_SUCCESS)
                    }
                } else {
                    findViewIdClick(getViewSendVerifyWelcome())
                    mHandler.postDelayed({findViewIdSetText("com.tencent.wework:id/ahx", mRunningTask?.content)
                        pressBack()}, RandUtil.randomInt(1000,3000))
                }
            }
        }
        return false
    }

    override fun onServiceConnected() {
        service = this
        Log.i(TAG,"onServiceConnected: ")
        AppConstants.init(this)
        mHandler.sendEmptyMessageDelayed(0, 1000)
//        val tashReceiver = IntentFilter()
//        tashReceiver.addAction("add_task")
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(
//                context: Context,
//                intent: Intent
//            ) {
//                val action = intent.action
//                if ("add_task" == action) {
//                    val tasks = intent.getStringExtra("tasks")
//                    val phone = tasks.split(",".toRegex()).toTypedArray()[0]
//                    val content = tasks.split(",".toRegex()).toTypedArray()[1]
//                    Log.d(
//                        TAG,
//                        "开始任务,phone$phone content:$content"
//                    )
//                    startTask(Task(phone, content))
//                }
//            }
//        }, tashReceiver)
//        startServer(9462, this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        service = this
        return Service.START_REDELIVER_INTENT
    }

    override fun onCreate() {
        super.onCreate()
        service = this
    }

    private fun log(msg: String) {
        Log.d(TAG, msg)
    }

    private var mRunningTask: Task? = null
    fun startTask(task: Task): Boolean {
        log("start task current task $mRunningTask new task ${task.phone} ${task.content}")
        if (mRunningTask == null) {
            mRunningTask = task
            if (ACTIVITY_CONTACT_SEND_VERIFY == mTopActivityName) {
                pressBack()
                mHandler.postDelayed(
                    { pressBack() },
                    1000
                )
            }
            mHandler.sendEmptyMessageDelayed(0, 3000)
//            if (WindowManager.checkFloatPermission(this)) {
//                FloatWindowManager.addBallView(this)
//            }
            return true
        }
        return false
    }

    fun stop(): Boolean {
        mRunningTask = null
        FloatWindowManager.setText("")
        return true
    }

    override fun onDestroy() {
        Log.i(
            TAG,
            "onDestroy: "
        )
        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this)
    }

    /**
     * 当前的窗口是否是全屏的
     */
    private fun isRootFullscreen() : Boolean{
        val rect = Rect()
        rootInActiveWindow.getBoundsInScreen(rect)
        return rect.bottom > 500
    }


    private var mTopActivityName: String? = null
    private var mTopPackageName: String? = null


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName.toString()
        if (packageName == WEWORK_PACKAGE) {
            Log.d("cuiqing", "onAccessibilityEvent event:${event.eventType} ${event.text}  ${event.source?.viewIdResourceName}")
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (event.packageName != null && event.className != null) {
                    if ("com.tencent.wework" == packageName) {
                        mTopPackageName = event.packageName.toString()
                        if (event.className.toString().endsWith("Activity")) {
                            mTopActivityName = event.className.toString()
                        }
                    } else {
                        if ("com.sohu.inputmethod.sogou.xiaomi" != packageName) {
                            mTopPackageName = null
                            mTopActivityName = null
                        }
                    }
//                                ComponentName componentName = new ComponentName(
//                        event.getPackageName().toString(),
//                        event.getClassName().toString()
//                );
//
//                ActivityInfo activityInfo = tryGetActivity(componentName);
//                boolean isActivity = activityInfo != null;
//                if (isActivity)
//                    Log.i(TAG, "top activity"+ componentName.flattenToShortString());
                }
            }
        }

    }

    /**
     * 查找文本的控件，并点击
     */
    private fun findTxtClick(nodeInfo: AccessibilityNodeInfo, txt: String) {
        val nodes =
            nodeInfo.findAccessibilityNodeInfosByText(txt)
        if (nodes == null || nodes.isEmpty()) return
        Log.i(
            TAG,
            "findTxtClick: " + txt + ", " + nodes.size + ", " + nodes
        )
        for (node in nodes) {
            if (node.isEnabled && node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

//    fun inputTap(x: Int, y: Int): String? {
//        val script = "input tap $x $y"
//        return execCommand(script)
//    }

    fun pressBack(){
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createClick(x: Float, y: Float): GestureDescription {
        // for a single tap a duration of 1 ms is enough
        val DURATION = 1
        val clickPath = Path()
        clickPath.moveTo(x, y)
        val clickStroke = StrokeDescription(clickPath, 0, DURATION.toLong())
        val clickBuilder = GestureDescription.Builder()
        clickBuilder.addStroke(clickStroke)
        return clickBuilder.build()
    }

    val callback = @RequiresApi(Build.VERSION_CODES.N)
    object : GestureResultCallback() {
        override fun onCancelled(gestureDescription: GestureDescription?) {
            super.onCancelled(gestureDescription)
        }

        override fun onCompleted(gestureDescription: GestureDescription?) {
            super.onCompleted(gestureDescription)
        }
    };

    @RequiresApi(Build.VERSION_CODES.N)
    fun tapViewById(viewId: String?) {
        val view = findViewIdFirst(viewId)
        val rect = Rect()
        view?.let {
            it.getBoundsInScreen(rect)
            inputTap(RandUtil.randomInt(rect.left, rect.right).toFloat(), RandUtil.randomInt(rect.top, rect.bottom).toFloat())
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun inputTap(x: Float, y: Float) {
        val result: Boolean = dispatchGesture(createClick(x, y), callback, null)
        Log.d(TAG, "Gesture dispatched? $result")
    }


    /**
     * 查找viewid,并点击，如果有多个，则点击第一个
     */
    private fun findViewIdClick(viewId: String?): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            if (nodes == null || nodes.isEmpty()) return false
            Log.i(TAG,"findTxtClick: " + viewId + ", " + nodes.size + ", " + nodes)
            nodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return false
    }

    /**
     * 查找viewid,并点击第一个找到的view的parent
     */
    private fun findViewIdParentClick(viewId: String): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            if (nodes == null || nodes.isEmpty()) return false
            Log.i(TAG,"findTxtClick: " + viewId + ", " + nodes.size + ", " + nodes)
            nodes[0].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return false
    }

    /**
     * 根据viewid查找第一个找到的控件
     */
    private fun findViewIdFirst(viewId: String?): AccessibilityNodeInfo? {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            if (nodes != null && nodes.isNotEmpty()) return nodes[0]
        }
        return null
    }

    /**
     * 获取第一个viewid的文本
     */
    private fun findViewIdText(viewId: String?): String? {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            if (nodes != null && !nodes.isEmpty()) return nodes[0].text.toString()
        }
        return null
    }

    private fun findViewIdSetText(viewId: String?, content: String?): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            if (nodes == null || nodes.isEmpty()) return false
            Log.i(
                TAG,
                "findTxtClick: " + viewId + ", " + nodes.size + ", " + nodes
            )
            //            nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, content);
            val arguments = Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                content
            )
            nodes[0].performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            //            nodes.get(0).setText(content);
            return true
        }
        return false
    }

    /**
     * 根据文本查找view
     * 查找安装,并模拟点击(findAccessibilityNodeInfosByText判断逻辑是contains而非equals)
     */
    private fun findViewByText(text: String): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByText(text)
            return nodes != null && nodes.isNotEmpty()
        }
        return false
    }

    /**
     * 根据文本查找view
     * 查找安装,并模拟点击(findAccessibilityNodeInfosByText判断逻辑是contains而非equals)
     */
    private fun findViewsByText(text: String): List<AccessibilityNodeInfo>? {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByText(text)
            if (!nodes.isNullOrEmpty()) {
                return nodes
            }
        }
        return null
    }

    /**
     * 检测view是否可见
     */
    private fun checkViewsIdExists(viewId: String?): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            return nodes != null && nodes.isNotEmpty()
        }
        return false
    }

    private fun findViewsById(viewId: String?): List<AccessibilityNodeInfo>? {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            return if (nodes != null && !nodes.isEmpty()) {
                nodes
            } else null
        }
        return null
    }

    override fun onInterrupt() {}

    companion object {
        var service: AccessibilityService? = null
        private const val TAG = "wework_assist"
        private const val ACTIVITY_MAIN = "com.tencent.wework.launch.WwMainActivity"
        private const val ACTIVITY_ADD_FRIEND = "com.tencent.wework.friends.controller.FriendAddMenu3rdActivity"
        private const val ACTIVITY_ADD_FRIEND_SEARCH = "com.tencent.wework.friends.controller.WechatFriendAddSearchActivity"
        private const val ACTIVITY_CONTACT_INFO = "com.tencent.wework.contact.controller.WechatContactInfoActivity"
        private const val ACTIVITY_CONTACT_SEND_VERIFY = "com.tencent.wework.friends.controller.WechatContactSendVerifyActivity"
        private const val ACTIVITY_CONTACT_SEND_EXISTS = "com.tencent.wework.contact.controller.ExternalWechatContactDetailActivity"
        private const val ACTIVITY_FRIEND_ADD_MULTI = "com.tencent.wework.friends.controller.FriendAddMultiIdentityActivity"
        private const val ACTIVITY_FRIEND_COMPANY = "com.tencent.wework.contact.controller.ContactDetailBriefInfoProfileActivity"
        private const val ACTIVITY_FRIEND_COMPANY_DETAIL = "com.tencent.wework.contact.controller.ContactDetailActivity"
    }
}