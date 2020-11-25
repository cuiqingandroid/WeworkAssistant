package com.cq.wechatworkassist.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.cq.wechatworkassist.task.Task
import java.util.*

object DBTask {
    private const val TABLE_NAME = "db_phone_task"
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

    fun onUpgrade( db: SQLiteDatabase,oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table $TABLE_NAME")
        createTable(db)
    }

    fun insertTasks(tasks: List<Task>) {
        try {
            val db = SqlHelper.getWritableDatabase()
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
            val tasks = mutableListOf<Task>()
            val db = SqlHelper.getReadableDatabase()
            val c =
                db.query(TABLE_NAME, null, null, null, null, null, null)
            c.use {
                while (c.moveToNext()) {
                    val task =
                        Task(
                            c.getString(1),
                            c.getString(2)
                        )
                    task.status = c.getString(3)
                    task.name = c.getString(4)
                    tasks.add(task)
                }
            }
            return tasks
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    fun queryUnDoneTask(): Task? {
        try {
            val db = SqlHelper.getReadableDatabase()
            val c = db.rawQuery("select * from $TABLE_NAME where status is NULL",
                null,
                null
            )
            c.use {
                while (c.moveToNext()) {
                    return Task( c.getString(1),c.getString(2))
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun updatePhoneStatus(phone: String, name:String?, status: Int) {
        try {
            val db = SqlHelper.getWritableDatabase()
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