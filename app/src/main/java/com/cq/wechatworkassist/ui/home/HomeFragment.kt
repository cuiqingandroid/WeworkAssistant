package com.cq.wechatworkassist.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cq.wechatworkassist.*
import com.cq.wechatworkassist.util.NetworkUtil
import com.cq.wechatworkassist.util.RootUtil
import com.cq.wechatworkassist.wework.WEWORK_VERSION
import com.cq.wechatworkassist.wework.isWeworkInstall
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


/**
 * 检测各种状态
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
        Log.d("cuiqing", NetworkUtil.getLocalIpAddress())
        Log.d("cuiqing", NetworkUtil.getSelfIp(requireActivity()))
        Log.d("cuiqing", "isroot:" + RootUtil.isRoot())
        setHasOptionsMenu(true)
    }

    private fun requestFloatPermission(context: Context?) {
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
            if (PermissionManager.checkFloatPermission(activity)) {
                Toast.makeText(activity, "悬浮窗权限申请成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "悬浮窗权限申请失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val isWeworkInstall = isWeworkInstall()
        Log.d("cuiqing", "企业微信是否安装$isWeworkInstall")
        if (isWeworkInstall) {
            view?.tvRootStatus?.text = WEWORK_VERSION
        } else {
            view?.tvRootStatus?.text = "企业微信未安装"
        }
        val isAccessOpen = AccessibilityUtil.isSettingOpen(AccessibilityService::class.java, context)
        view?.tvAccessStatus?.text = if (isAccessOpen) "已开启" else "请到设置开启智能辅助"
        if (PermissionManager.checkFloatPermission(activity)) {
            tvFloatStatus.text = "已获得"
        } else {
            tvFloatStatus.text = "未获得"
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}