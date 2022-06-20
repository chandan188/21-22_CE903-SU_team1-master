package com.example.itracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import android.os.Environment;
import android.util.Log;

import com.example.itracker.model.Sensor;
import com.example.itracker.params.Params;

import java.io.File;
import java.io.PrintWriter;


public class iDBHandler extends SQLiteOpenHelper {

    public iDBHandler(@Nullable Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE "
                + Params.TABLE_NAME + "("
                + Params.USER_ID + " TEXT,"
                + Params.USER_ACTIVITY + " TEXT,"
                + Params.WATCH_ACC_X + " TEXT,"
                + Params.WATCH_ACC_Y + " TEXT,"
                + Params.WATCH_ACC_Z + " TEXT,"
                + Params.WATCH_BVP + " TEXT,"
                + Params.MOB_ACC_X + " TEXT,"
                + Params.MOB_ACC_Y + " TEXT,"
                + Params.MOB_ACC_Z + " TEXT,"
                + Params.MOB_GYRO_X + " TEXT,"
                + Params.MOB_GYRO_Y + " TEXT,"
                + Params.MOB_GYRO_Z + " TEXT,"
                + Params.WATCH_TIMESTAMP + " TEXT"
                + ")";

        Log.i("e4_db", Params.TABLE_NAME +" table created successfully");
        db.execSQL((create));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addSensorData(Sensor sensor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.USER_ID, sensor.getUser_id() );
        values.put(Params.USER_ACTIVITY, sensor.getUser_activity());
        values.put(Params.WATCH_ACC_X, sensor.getW_acc_x() );
        values.put(Params.WATCH_ACC_Y, sensor.getW_acc_y() );
        values.put(Params.WATCH_ACC_Z, sensor.getW_acc_z() );
        values.put(Params.WATCH_BVP, sensor.getW_bvp() );
        values.put(Params.MOB_ACC_X,sensor.getM_acc_x());
        values.put(Params.MOB_ACC_Y,sensor.getM_acc_y());
        values.put(Params.MOB_ACC_Z,sensor.getM_acc_z());
        values.put(Params.MOB_GYRO_X,sensor.getM_gyro_x());
        values.put(Params.MOB_GYRO_Y,sensor.getM_gyro_y());
        values.put(Params.MOB_GYRO_Z,sensor.getM_gyro_z());
        values.put(Params.WATCH_TIMESTAMP, sensor.getW_timestamp() );
        db.insert(Params.TABLE_NAME,null,values);
        db.close();
    }

    public int getCount(){
        String query = "Select * from " + Params.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        return cursor.getCount();
    }
}

