package com.cq.wechatworkassist

interface TaskInterface {
    fun startTask(task: Task) : Boolean
    fun stop(): Boolean
}