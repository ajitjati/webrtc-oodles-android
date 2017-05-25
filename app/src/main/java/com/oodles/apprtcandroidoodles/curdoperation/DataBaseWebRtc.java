package com.oodles.apprtcandroidoodles.curdoperation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oodles.apprtcandroidoodles.ConfigAppRtc;

/**
 * Created by ankita on 24/4/17.
 */

public class DataBaseWebRtc extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = ConfigAppRtc.DB_NAME;

    public DataBaseWebRtc(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableContact(db);
    }

    private void createTableContact(SQLiteDatabase db) {
        String CREATE_CONTACT_TABLE = "CREATE TABLE " + ConfigAppRtc.CONTACT_TABLE + "(" + ConfigAppRtc.UNIQUEID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + ConfigAppRtc.CONTACT_ID + " TEXT," + ConfigAppRtc.CONTACT_NAME + " TEXT," + ConfigAppRtc.CONTACT_NUMBER + " TEXT," + ConfigAppRtc.PROFILE_IMAGE + " TEXT,"
                + ConfigAppRtc.ONLINE_STATUS + " TEXT, unique (" + ConfigAppRtc.CONTACT_NUMBER + "))";
        db.execSQL(CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ConfigAppRtc.CONTACT_TABLE);
    }

}
