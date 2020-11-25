package com.cq.wechatworkassist.wework

import android.content.Context
import android.content.pm.PackageManager
import com.cq.wechatworkassist.util.PackageUtil


var WEWORK_VERSION:String? = ""
var companyName = ""
var context: Context? = null

const val WEWORK_PACKAGE = "com.tencent.wework"

fun initWework(context: Context) {
    WEWORK_VERSION = PackageUtil.getVersion(context, WEWORK_PACKAGE)
}

const val WEWORK_UI_TEXT_MENU_ADD_WECHAT = "加微信"
const val WEWORK_UI_TEXT_OPERATION_LIMIT = "操作异常，暂无法使用"
const val WEWORK_UI_TEXT_USER_NOT_EXISTS = "该用户不存在"
const val WEWORK_UI_TEXT_SEND_VERIFY_BUTTON = "该用户不存在"

fun getSupportVersion() : List<String>{
    return listOf(VERSION_3031, VERSION_3036)
}

const val VERSION_3036 = "3.0.36"
const val VERSION_3031 = "3.0.31"

/**
 * 企业微信是否安装
 */
fun isWeworkInstall(): Boolean {
    try {
        val packageInfo = context?.packageManager?.getPackageInfo(WEWORK_PACKAGE, 0)
        if (packageInfo != null) {
            return true
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace();
    }
    return true;
}

/**
 * 主界面，+号按阿牛
 */
fun getViewIdAdd() :String?{
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/i6d"
        VERSION_3031 -> "com.tencent.wework:id/hxm"
        else -> null
    }
}
/**
 * 加微信才菜单弹框，可以有多个，找第二个
 */
fun getViewIdAddWechat() :String?{
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/egd"
        VERSION_3031 -> "com.tencent.wework:id/ec6"
        else -> null
    }
}

/**
 * 加好友第一个页面【通过手机号搜索添加微信】
 */
fun getViewIdSearchPhone() :String?{
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/gq2"
        VERSION_3031 -> "com.tencent.wework:id/gif"
        else -> null
    }
}

/**
 * 加好友搜索手机号【需要填入手机号的输入框】
 */
fun getViewIdEdSearchPhone() :String?{
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/gpg"
        VERSION_3031 -> "com.tencent.wework:id/ghu"
        else -> null
    }
}

/**
 * 搜索手机号【输入手机号后，开始搜索按钮】
 */
fun getViewIdLayoutSearchPhoneResult() :String?{
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/gjs"
        VERSION_3031 -> "com.tencent.wework:id/dqw"
        else -> null
    }
}

/**
 * 搜索结果，电话号码的view的父容器
 */
fun getViewContactInfoPhone() : String? {

    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/gbx"
        VERSION_3031 -> "com.tencent.wework:id/g5s"
        else -> null
    }
}

/**
 * 搜索结果，添加按钮
 */
fun getViewContactInfoAddBtn() : String? {

    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/h4"
        VERSION_3031 -> "com.tencent.wework:id/h2"
        else -> null
    }
}
/**
 * 搜索结果，微信昵称
 */
fun getViewContactInfoName() : String? {

    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/hnm"
        VERSION_3031 -> "com.tencent.wework:id/hfb"
        else -> null
    }
}

/**
 * 发送好友验证，企业名称
 */
fun getViewSendVerifyCompany() : String? {
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/ajf"
        VERSION_3031 -> "com.tencent.wework:id/hfb"
        else -> null
    }
}
/**
 * 发送好友验证，【添加语】
 */
fun getViewSendVerifyWelcome() : String? {
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/coa"
        VERSION_3031 -> "com.tencent.wework:id/ahx"
        else -> null
    }
}
/**
 * 发送好友验证，【发送按钮】
 */
fun getViewSendVerifyButton() : String? {
    return when (WEWORK_VERSION){
        VERSION_3036 -> "com.tencent.wework:id/d3v"
        VERSION_3031 -> "com.tencent.wework:id/d12"
        else -> null
    }
}
