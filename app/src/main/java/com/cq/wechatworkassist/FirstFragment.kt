package com.cq.wechatworkassist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_first.view.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            execCommand("ls")
//        }
        Log.d("cuiqing", NetworkUtil.getLocalIpAddress())
        Log.d("cuiqing", NetworkUtil.getSelfIp(context!!))
    }

    override fun onStart() {
        super.onStart()
        val isRoot = RootCheck.hasRootPermission()
        Log.d("cuiqing", "root权限$isRoot")
        if (isRoot) {
            view?.tvRootStatus?.text = "已获得"
        } else {
            view?.tvRootStatus?.text = "请先root手机并授予root权限"
        }
        val isAccessOpen = AccessibilityUtil.isSettingOpen(AccessibilityService::class.java, activity)
        view?.tvAccessStatus?.text = if(isAccessOpen) "已开启" else "请到设置开启智能辅助"
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
}