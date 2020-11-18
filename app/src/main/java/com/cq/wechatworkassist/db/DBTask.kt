package com.cq.wechatworkassist.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.cq.wechatworkassist.Task
import com.cq.wechatworkassist.db.SqlHelper
import java.util.*

/**
 * Created by cuiqing on 2015/8/5.
 */
object DBTask {
    const val TABLE_NAME = "db_phone_task"
    @JvmStatic
    fun createTable(db: SQLiteDatabase) {
        // 创建表
        val sql = StringBuilder()
        sql.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME)
        sql.append(" ( ")
        sql.append(Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT ")
        sql.append(",")
        sql.append(Column.PHONE + " VARCHAR(15) UNIQUE")
        sql.append(",")
        sql.append(Column.CONTENT + " VARCHAR(50)")
        sql.append(",")
        sql.append(Column.STATUS + " VARCHAR(20)")
        sql.append(",")
        sql.append(Column.NAME + " VARCHAR(20)")
        sql.append(")")
        db.execSQL(sql.toString())
    }

    fun insertTasks(tasks: List<Task>) {
        try {
            val db = SqlHelper.getInstance().readableDatabase
            for (task in tasks) {
                val cv = ContentValues()
                cv.put(Column.PHONE, task.phone)
                cv.put(Column.CONTENT, task.content)
                db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
            }
        } catch (e: Throwable) {
            //因为goods_id是唯一的，所以相同会失败，捕获异常
            Log.d("cuiqing", "插入数据库异常")
        }
    }

    fun queryTasks(): List<Task> {
        try {
            val tasks: MutableList<Task> =
                ArrayList()
            val db = SqlHelper.getInstance().readableDatabase
            val c =
                db.query(TABLE_NAME, null, null, null, null, null, null)
            while (c.moveToNext()) {
                val task =
                    Task(c.getString(1), c.getString(2))
                task.status = c.getString(3)
                task.name = c.getString(4)
                tasks.add(task)
            }
            return tasks
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    fun queryUnDoneTask(): Task? {
        try {
            val db = SqlHelper.getInstance().readableDatabase
            val c = db.rawQuery("select * from $TABLE_NAME where status is NULL",
                null,
                null
            )
            while (c.moveToNext()) {
                return Task(c.getString(1), c.getString(2))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun updatePhoneStatus(phone: String, name:String?, status: String) {
        try {
            val db = SqlHelper.getInstance().readableDatabase
            val cv = ContentValues()
            cv.put(Column.PHONE, phone)
            cv.put(Column.NAME, name?: "")
            cv.put(Column.STATUS, status)
            db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        } catch (e: Throwable) {
            //因为goods_id是唯一的，所以相同会失败，捕获异常
            Log.d("cuiqing", "插入数据库异常")
        }
    }

    object Column {
        const val ID = "id"
        const val PHONE = "phone"
        const val CONTENT = "content"
        const val STATUS = "status"
        const val NAME = "nickname"
    }
}