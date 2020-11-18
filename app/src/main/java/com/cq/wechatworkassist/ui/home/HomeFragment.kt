package com.cq.wechatworkassist.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cq.wechatworkassist.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            execCommand("ls")
//        }
        Log.d("cuiqing", NetworkUtil.getLocalIpAddress())
        Log.d(
            "cuiqing",
            NetworkUtil.getSelfIp(requireActivity())
        )
        Log.d("cuiqing", "isroot:" + RootCheck.isRoot())



//        view.findViewById<Button>(R.id.btnStart).setOnClickListener {
//            val intent = Intent("add_task")
//            intent.putExtra("tasks", "15578314513,你好")
//            activity?.sendBroadcast(intent)
//        }

    }

    fun requestFloatPermission(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent2 = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intent2, 1)
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + context?.packageName)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (WindowManager.checkFloatPermission(activity)) {
                Toast.makeText(activity, "悬浮窗权限申请成功", Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(activity, "悬浮窗权限申请失败", Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val isRoot = RootCheck.hasRootPermission()
        Log.d("cuiqing", "root权限$isRoot")
        if (isRoot) {
            view?.tvRootStatus?.text = "已获得"
        } else {
            view?.tvRootStatus?.text = "无root权限"
        }
        val isAccessOpen =
            AccessibilityUtil.isSettingOpen(
                AccessibilityService::class.java,
                activity
            )
        view?.tvAccessStatus?.text = if (isAccessOpen) "已开启" else "请到设置开启智能辅助"
//        if (AccessibilityUtil.checkSetting(
//                activity,
//                AccessibilityService::class.java
//            )
//        ) {
            if (WindowManager.checkFloatPermission(
                    activity
                )
            ) {
                tvFloatStatus.text = "已获得"
            } else {
//                requestFloatPermission(activity)
//                showFloatDialog()
                tvFloatStatus.text = "未获得"
            }
//        }
//        execCommand("input keyevent 4")
    }

    fun showFloatDialog() {
        AlertDialog.Builder(activity)
            .setTitle("权限设置")
            .setMessage("找到【" + getString(R.string.aby_label) + "】并授权【悬浮窗】权限")
            .setPositiveButton(
                R.string.common_ok
            ) { _, _ -> requestFloatPermission(activity) }
            .setCancelable(false)
            .show()
    }

}