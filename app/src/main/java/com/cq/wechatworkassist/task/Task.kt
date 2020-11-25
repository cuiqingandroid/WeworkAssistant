package com.cq.wechatworkassist.task

class Task(@JvmField var phone: String, @JvmField var content: String?) {
    @JvmField
    var name: String? = null
    var status: String? = null
}