package com.cq.wechatworkassist.task

class Task(@JvmField var phone: String, @JvmField var content: String?) {
    @JvmField
    var name: String? = null
    var status: String? = null

    /**
     * 运行时状态，用于判断是否已经搜索过
     */
    var hasSearched: Boolean = false
}