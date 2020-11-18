package com.cq.wechatworkassist

import android.content.Context
import com.cq.wechatworkassist.db.DBTask

object TaskManager {

    private lateinit var mContext: Context

    var isRunning: Boolean = false

    fun init(context: Context) {
        mContext = context.applicationContext
    }

    fun importData(taskList: List<Task>) {
        DBTask.insertTasks(taskList)
    }

    fun onTaskEnd(task: String, name:String?, status: String) {
        DBTask.updatePhoneStatus(task, name, status)
        if (isRunning) {
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