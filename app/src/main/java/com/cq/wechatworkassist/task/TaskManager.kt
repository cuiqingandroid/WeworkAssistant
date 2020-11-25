package com.cq.wechatworkassist.task

import com.cq.wechatworkassist.AccessibilityService
import com.cq.wechatworkassist.db.DBTask

object TaskManager {

    var isRunning: Boolean = false

    fun importData(taskList: List<Task>) {
        DBTask.insertTasks(taskList)
    }

    fun onTaskEnd(task: String, name:String?, status: Int) {
        if (STATUS_OPERATION_LIMIT != status) {
            DBTask.updatePhoneStatus(task, name, status)
        }
        if (STATUS_OPERATION_LIMIT == status){
            stop()
        } else if (isRunning) {
            val undoneTask = DBTask.queryUnDoneTask()
            if (undoneTask != null) {
                AccessibilityService.service?.startTask(undoneTask)
            } else {
                stop()
            }
        }
    }

    fun getAllTask(): List<Task>{
        return DBTask.queryTasks()
    }
    fun start() : Boolean{
        isRunning = true
        val undoneTask = DBTask.queryUnDoneTask()
        if (undoneTask != null) {
            AccessibilityService.service?.startTask(undoneTask)
            return true
        } else {
            // 没有需要执行的任务
            isRunning = false
            return false
        }
    }

    fun stop(): Boolean {
        isRunning = false
        return AccessibilityService.service?.stop() ?: false
    }
}