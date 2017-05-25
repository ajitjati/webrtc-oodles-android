package com.oodles.apprtcandroidoodles.curdoperation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.oodles.apprtcandroidoodles.ConfigAppRtc;
import com.oodles.apprtcandroidoodles.login.Contact;

import java.util.ArrayList;


/**
 * Created by ankita on 24/4/17.
 */

public class CurOperationWebRtc {
    Context mContext;

    public CurOperationWebRtc(Context context) {
        mContext = context;
    }

    // Uri profileImage,
    public void insertContacts(long contactId, String contactName, String contactNumber, Uri profileImage, String onlineStatus) {
        DataBaseWebRtc db = new DataBaseWebRtc(mContext);
        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigAppRtc.CONTACT_ID, contactId);
        values.put(ConfigAppRtc.CONTACT_NAME, contactName);
        values.put(ConfigAppRtc.CONTACT_NUMBER, contactNumber);
        values.put(ConfigAppRtc.PROFILE_IMAGE, profileImage.toString());
        values.put(ConfigAppRtc.ONLINE_STATUS, onlineStatus);
        database.insert(ConfigAppRtc.CONTACT_TABLE, null, values);
        Log.e("entryryryryyryr", contactId + contactName);
        db.close();
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        String selectQuery = "SELECT  * FROM " + ConfigAppRtc.CONTACT_TABLE;
        DataBaseWebRtc dataBaseSchema = new DataBaseWebRtc(mContext);
        SQLiteDatabase db = dataBaseSchema.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setContactId(cursor.getLong(cursor.getColumnIndex(ConfigAppRtc.CONTACT_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.CONTACT_NAME)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.CONTACT_NUMBER)));
                contact.setProfilePic(Uri.parse(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.PROFILE_IMAGE))));
                contact.setOnline(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.ONLINE_STATUS)));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }

    public ArrayList<Contact> getOnlineContacts(String onlineUsers) {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        String selectQuery = "SELECT  * FROM " + ConfigAppRtc.CONTACT_TABLE + " where " + ConfigAppRtc.ONLINE_STATUS + " = '" + onlineUsers + "'";
        DataBaseWebRtc dataBaseSchema = new DataBaseWebRtc(mContext);
        SQLiteDatabase db = dataBaseSchema.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setContactId(cursor.getLong(cursor.getColumnIndex(ConfigAppRtc.CONTACT_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.CONTACT_NAME)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.CONTACT_NUMBER)));
                contact.setProfilePic(Uri.parse(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.PROFILE_IMAGE))));
                contact.setOnline(cursor.getString(cursor.getColumnIndex(ConfigAppRtc.ONLINE_STATUS)));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }

    public void updateOnlineStatus(String phoneNumber, String aTrue) {
        DataBaseWebRtc db = new DataBaseWebRtc(mContext);
        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigAppRtc.ONLINE_STATUS, aTrue);
        database.update(ConfigAppRtc.CONTACT_TABLE, values, "contactNumber=?", new String[]{phoneNumber});
        db.close();
    }
}
