package com.cq.wechatworkassist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.cq.wechatworkassist.App;


public class SqlHelper extends SQLiteOpenHelper {

    private static SqlHelper mInstance= null;
    private static final String DB_NAME = "db_task.db";
    private static final int DB_VERSION = 1;
    public static SqlHelper getInstance(){
        if (mInstance == null){
            mInstance = new SqlHelper(App.mApp, DB_NAME, null, DB_VERSION);
        }
        return mInstance;
    }

    public SqlHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DBTask.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
