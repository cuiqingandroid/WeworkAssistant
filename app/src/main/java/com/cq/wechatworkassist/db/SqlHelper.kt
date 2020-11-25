package com.cq.wechatworkassist.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import com.cq.wechatworkassist.App
import com.cq.wechatworkassist.db.DBTask.createTable

class SqlHelper( context: Context?,name: String?,factory: CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int,newVersion: Int) {
        DBTask.onUpgrade(db, oldVersion, newVersion)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        private const val DB_NAME = "db_task.db"
        private const val DB_VERSION = 2
        private var sqlHelper: SqlHelper= SqlHelper(App.mApp, DB_NAME,null,DB_VERSION)

        fun getWritableDatabase(): SQLiteDatabase {
            return sqlHelper.writableDatabase
        }
        fun getReadableDatabase(): SQLiteDatabase {
            return sqlHelper.readableDatabase
        }
    }
}