package com.cq.wechatworkassist.task

import com.cq.wechatworkassist.task.Task

interface TaskInterface {
    fun startTask(task: Task) : Boolean
    fun stop(): Boolean
}