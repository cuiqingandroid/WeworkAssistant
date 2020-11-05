package com.cq.wechatworkassist

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.cq.wechatworkassist.HttpServer.Companion.startServer
import com.cq.wechatworkassist.NetApi.uploadStatus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * 辅助服务自动安装APP，该服务在单独进程中允许
 */
class AccessibilityService : android.accessibilityservice.AccessibilityService(),TaskInterface {
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (!checkUi()) {
                sendEmptyMessageDelayed(0, Util.randomInt(1500, 3000))
            }
        }
    }

    private fun checkUi() : Boolean{
        if (mRunningTask != null) {
//            if (isKeyboardOpen()) {
//                pressBack()
//                return false
//            }
            mTopActivityName = tryGetActivity()
            if (rootInActiveWindow == null){
                inputTap(Util.randomInt(700), Util.randomInt(120))
                mHandler.sendEmptyMessageDelayed(0, Util.randomLong(500))
                return true
            }
            if (!isRootFullscreen()) {
                pressBack()
                return false
            }
            log("checkui ${mRunningTask?.phone} topActivityName:$mTopActivityName")
            // 检测企业微信是否开启
            if (mTopActivityName?.contains("com.tencent.wework") == false) {
                startWechatWorkMain()
                mHandler.sendEmptyMessageDelayed(0, 20000)
                mHandler.postDelayed(
                    { if (ACTIVITY_CONTACT_SEND_VERIFY == mTopActivityName) {
                        inputKeyevent(KeyEvent.KEYCODE_BACK)
                    } },
                    Util.randomInt(1500,2500)
                )
                return true
            }
            if (ACTIVITY_MAIN == mTopActivityName) {
                // 判断加微信菜单是否显示
                if (findText("加微信")) {
                    val rootNode = rootInActiveWindow
                    if (rootNode != null) {
                        val nodes =
                            rootNode.findAccessibilityNodeInfosByViewId("com.tencent.wework:id/ec6")
                        if (nodes != null && !nodes.isEmpty()) {
//                                Log.i(TAG, "findTxtClick: " + viewId + ", " + nodes.size() + ", " + nodes);
                            nodes[1].parent
                                .performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                    }
                } else {
                    findViewIdClick("com.tencent.wework:id/hxm")
                }
            }
            if (ACTIVITY_ADD_FRIEND == mTopActivityName) {
//                    findViewIdParentClick("com.tencent.wework:id/gif")
                val view = findViewIdFirst("com.tencent.wework:id/gif")
                if (view != null) {
                    inputTap(480, 360)
                }
            }
            if (ACTIVITY_FRIEND_ADD_MULTI == mTopActivityName) {
                findViewIdClick("com.tencent.wework:id/bs9")
            }
            if (ACTIVITY_ADD_FRIEND_SEARCH == mTopActivityName) {
                val unexists = findViewIdText("com.tencent.wework:id/bjr")
                if (unexists == "该用户不存在") {
                    val phone = mRunningTask!!.phone
                    val name = mRunningTask!!.name
                    mRunningTask = null

                    inputKeyevent(KeyEvent.KEYCODE_BACK)
                    mHandler.postDelayed(
                        { inputKeyevent(KeyEvent.KEYCODE_BACK) },
                        2000
                    )
                    uploadStatus(phone, name, "手机号查不到")
                    return false
                }

                val searchTextView = findViewIdFirst("com.tencent.wework:id/ghu")
                if (searchTextView == null) {
                    inputKeyevent(KeyEvent.KEYCODE_BACK)
                    return false
                }
                if (searchTextView.text.toString() == mRunningTask?.phone) {
                    // 点击搜索结果
                    inputTap(480, 280)
//                    findViewIdClick("com.tencent.wework:id/dqw")
                } else {
                    findViewIdSetText("com.tencent.wework:id/ghu", mRunningTask!!.phone)
                }
            }
            if (ACTIVITY_CONTACT_SEND_EXISTS == mTopActivityName) {
                // 已存在，上报已存在
                val phone = mRunningTask!!.phone
                val name = mRunningTask!!.name
                mRunningTask = null
                inputKeyevent(KeyEvent.KEYCODE_BACK)
                mHandler.postDelayed(
                    { inputKeyevent(KeyEvent.KEYCODE_BACK) },
                    2000
                )
                uploadStatus(phone, name, "已经是好友")
            }
            if (ACTIVITY_FRIEND_COMPANY_DETAIL == mTopActivityName) {
                // 已存在，上报已存在
                val phone = mRunningTask!!.phone
                val name = mRunningTask!!.name
                mRunningTask = null
                inputKeyevent(KeyEvent.KEYCODE_BACK)
                mHandler.postDelayed(
                    { inputKeyevent(KeyEvent.KEYCODE_BACK) },
                    2000
                )
                uploadStatus(phone, name, "企业微信用户不加好友")
            }
            if (ACTIVITY_FRIEND_COMPANY == mTopActivityName) {
                // 已存在，上报已存在
                val phone = mRunningTask!!.phone
                val name = mRunningTask!!.name
                mRunningTask = null
                inputKeyevent(KeyEvent.KEYCODE_BACK)
                mHandler.postDelayed(
                    { inputKeyevent(KeyEvent.KEYCODE_BACK) },
                    2000
                )
                uploadStatus(phone, name, "已经是企业微信好友")
            }
            if (ACTIVITY_CONTACT_INFO == mTopActivityName) {
                val hasAdd = checkViewsIdExists("com.tencent.wework:id/h2")
                mRunningTask!!.name = findViewIdText("com.tencent.wework:id/hfb")
                val view = findViewIdFirst("com.tencent.wework:id/g5s")
                if (!hasAdd || view == null){
                    inputKeyevent(KeyEvent.KEYCODE_BACK)
                    return false
                }
                if (hasAdd) {
                    if (view.getChild(0).text.toString() == mRunningTask!!.phone) {
                        findViewIdClick("com.tencent.wework:id/h2")
                    } else {
                        inputKeyevent(KeyEvent.KEYCODE_BACK)
                        mHandler.postDelayed({inputKeyevent(KeyEvent.KEYCODE_BACK)}, Util.randomInt(500, 1000))
                    }
                }
            }
            if (ACTIVITY_CONTACT_SEND_VERIFY == mTopActivityName) {
                if (findViewsById("com.tencent.wework:id/ahx") == null) {
                    findViewIdParentClick("com.tencent.wework:id/ahz")
//                        inputTap(
//                            942 + Random().nextInt(20),
//                            1142 + Random().nextInt(20)
//                        )
                } else {
                    val views = findViewsById("com.tencent.wework:id/ahx")
                    if (views != null && views[0].text.toString() != mRunningTask!!.content) {
                        findViewIdSetText("com.tencent.wework:id/ahx", mRunningTask!!.content)
                        inputKeyevent(KeyEvent.KEYCODE_BACK)
                    } else if (findViewIdClick("com.tencent.wework:id/d12")) {
                        val phone = mRunningTask!!.phone
                        val name = mRunningTask!!.name
                        mRunningTask = null
//                            inputTap(300 + Random().nextInt(500), 720)
//                            inputKeyevent(KeyEvent.KEYCODE_BACK)
                        mHandler.postDelayed({inputKeyevent(KeyEvent.KEYCODE_BACK)},Util.randomLong(500,1000))
                        mHandler.postDelayed({inputKeyevent(KeyEvent.KEYCODE_BACK)},Util.randomLong(2000,3000))
                        // 上报成功
                        uploadStatus(phone, name, "添加成功")
                    }
                }
            }
        }
        return false
    }

    override fun onServiceConnected() {
        Log.i(TAG,"onServiceConnected: ")
        AppConstants.init(this)
        mHandler.sendEmptyMessageDelayed(0, 1000)
        val tashReceiver = IntentFilter()
        tashReceiver.addAction("add_task")
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                val action = intent.action
                if ("add_task" == action) {
                    val tasks = intent.getStringExtra("tasks")
                    val phone = tasks.split(",".toRegex()).toTypedArray()[0]
                    val content = tasks.split(",".toRegex()).toTypedArray()[1]
                    Log.d(
                        TAG,
                        "开始任务,phone$phone content:$content"
                    )
                    startTask(Task(phone, content))
                }
            }
        }, tashReceiver)
        startServer(9462, this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_REDELIVER_INTENT
    }

    private fun log(msg: String) {
        Log.d(TAG, msg)
    }

    private var mRunningTask: Task? = null
    override fun startTask(task: Task): Boolean {
        log("start task current task $mRunningTask new task ${task.phone} ${task.content}")
        if (mRunningTask == null) {
            mRunningTask = task
            if (ACTIVITY_CONTACT_SEND_VERIFY == mTopActivityName) {
                inputKeyevent(KeyEvent.KEYCODE_BACK)
                mHandler.postDelayed(
                    { inputKeyevent(KeyEvent.KEYCODE_BACK) },
                    1000
                )
            }
            return true
        }
        return false
    }

    override fun stop(): Boolean {
        mRunningTask = null
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

    /**
     * 获取当前顶部activity
     */
    private fun tryGetActivity(): String? {
        val ret = execCommand("dumpsys activity top | grep ACTIVITY")
        if (ret != null && ret != "") {
            val items = ret.trim().split("\n")
            val arr =items[items.size -1].split(" ")
            if (arr.size > 1) {
                return arr[1].replace("/", "")
            }
        }
        return null
    }

    /**
     * 判断键盘是否可见
     */
    private fun isKeyboardOpen() : Boolean{
        val ret = execCommand("dumpsys input_method |grep mInputShown=true")
        return !ret.isNullOrBlank()
    }


    private var mTopActivityName: String? = null
    private var mTopPackageName: String? = null
    private fun startWechatWorkMain() {
//        val intent = Intent(Intent.ACTION_MAIN)
//        intent.component = ComponentName(
//            "com.tencent.wework",
//            "com.tencent.wework.launch.LaunchSplashActivity"
//        )
//        intent.addCategory(Intent.CATEGORY_LAUNCHER)
////        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
        execCommand("am start -n com.tencent.wework/com.tencent.wework.launch.LaunchSplashActivity")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName.toString()
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
                //                ComponentName componentName = new ComponentName(
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

    fun inputTap(x: Int, y: Int): String? {
        val script = "input tap $x $y"
        return execCommand(script)
    }

    fun pressBack(){
        inputKeyevent(KeyEvent.KEYCODE_BACK)
    }

    fun inputKeyevent(keyevent: Int): String? {
        val script = "input keyevent $keyevent"
        return execCommand(script)
    }

    fun execCommand(command: String): String? {
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
                s += """
                    $line

                    """.trimIndent()
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

    /**
     * 查找viewid,并点击，如果有多个，则点击第一个
     */
    private fun findViewIdClick(viewId: String): Boolean {
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
    private fun findViewIdFirst(viewId: String): AccessibilityNodeInfo? {
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
    private fun findViewIdText(viewId: String): String? {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            if (nodes != null && !nodes.isEmpty()) return nodes[0].text.toString()
        }
        return null
    }

    private fun findViewIdSetText(viewId: String, content: String): Boolean {
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

    private fun findText(txt: String): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByText(txt)
            return nodes != null && !nodes.isEmpty()
        }
        return false
    }

    /**
     * 检测view是否可见
     */
    private fun checkViewsIdExists(viewId: String): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes =
                rootNode.findAccessibilityNodeInfosByViewId(viewId)
            return nodes != null && !nodes.isEmpty()
        }
        return false
    }

    private fun findViewsById(viewId: String): List<AccessibilityNodeInfo>? {
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